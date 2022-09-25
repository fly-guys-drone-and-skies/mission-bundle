package edu.rit.se.sars.mission.flight;

import edu.rit.se.sars.communication.message.internal.command.ChangeZoomLevelCommand;
import edu.rit.se.sars.communication.message.internal.command.MoveGimbalCommand;
import edu.rit.se.sars.communication.message.internal.command.MoveToCommand;
import edu.rit.se.sars.communication.proto.ipc.command.Command;
import edu.rit.se.sars.communication.proto.ipc.command.RouteOuterClass;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.drone.DjiMavicDrone;
import edu.rit.se.sars.drone.Drone;
import edu.rit.se.sars.drone.exception.DroneCommandException;
import edu.rit.se.sars.mission.flight.path.FlightPath;
import edu.rit.se.sars.mission.flight.path.FlightPathPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

/**
 * Controller for execution of flight plan
 */
public class FlightPathController extends Observable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FlightPathController.class);

    private final Drone drone;

    private GeoLocation2D homeLocation;
    private volatile FlightPath flightPath = new FlightPath();
    private FlightPathPoint currentPoint = null;
    private boolean inAir = false;

    public FlightPathController(Drone drone) {
        this.drone = drone;
    }

    /**
     * @return Drone controlled by this flight path controller
     */
    public Drone getDrone() {
        return this.drone;
    }

    /**
     * @return true if drone is currently moving between points, false otherwise
     */
    public boolean isInAir() {
        return this.inAir;
    }

    /**
     * Get drone's home location. If not yet set, its current location will be set as its home location.
     * @return Home location
     */
    public GeoLocation2D getHomeLocation() {
        if (this.homeLocation == null) {
            this.homeLocation = new GeoLocation2D(this.drone.getLocation());
        }
        return this.homeLocation;
    }

    /**
     * Override current flight path
     * NOTE: Will only take affect after current point is reached
     * @param flightPath New flight path
     */
    public void setFlightPath(FlightPath flightPath) {
        this.flightPath = flightPath;
    }

    /**
     * @return Point currently being processed
     */
    public FlightPathPoint getCurrentPoint() {
        if (this.currentPoint == null) {
            return this.flightPath.getPoints().peek();
        } else {
            return this.currentPoint;
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!this.inAir && this.flightPath.size() > 0) {
                    logger.debug("Starting flight path");

                    this.homeLocation = new GeoLocation2D(this.drone.getLocation());
                    if (drone.getClass() == DjiMavicDrone.class) {
                        //Prebuild the actions required for the route, since they don't have real time pathing.
                        List<Command> commands = new ArrayList<>();
                        List<FlightPathPoint> startingPathPoints = flightPath.getPointList();
                        for (FlightPathPoint point : startingPathPoints) {
                            commands.add(new MoveGimbalCommand(point.getGimbalOrientation()).toProtobuf());
                            commands.add(new ChangeZoomLevelCommand(point.getZoomLevel()).toProtobuf());
                            commands.add(new MoveToCommand(point.getLocation()).toProtobuf());
                        }
                        RouteOuterClass.Route route = RouteOuterClass.Route.newBuilder().addAllActions(commands).build();
                        this.drone.sendRoute(route);
                    }
                    this.drone.takeoff();
                    this.inAir = true;
                }

                Optional<FlightPathPoint> maybePoint = this.flightPath.getNextPoint();
                if (maybePoint.isPresent()) {
                    FlightPathPoint point = maybePoint.get();
                    this.currentPoint = point;

                    logger.debug("Handling point: {}", point);

                    // Change camera settings before initiating flight to next point
                    drone.moveGimbalTo(point.getGimbalOrientation());
                    drone.setZoomLevel(point.getZoomLevel());

                    // Move to point location with specified heading
                    drone.moveTo(point.getLocation(), point.getHeadingDegrees());

                    // Notify observers of completed point
                    this.setChanged();
                    this.notifyObservers(point);
                } else if (inAir) {
                    // No more points left to visit - land at current location
                    logger.debug("End of path reached");

                    drone.land();
                    this.inAir = false;
                }
            } catch (DroneCommandException e) {
                logger.error("Drone command failed", e);
            }
        }
    }
}
