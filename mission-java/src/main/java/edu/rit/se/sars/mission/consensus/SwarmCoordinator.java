package edu.rit.se.sars.mission.consensus;

import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.swarm.*;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityRegistry;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.drone.Drone;
import edu.rit.se.sars.mission.Mission;
import edu.rit.se.sars.mission.flight.FlightPathController;
import edu.rit.se.sars.mission.flight.path.FlightPath;
import edu.rit.se.sars.mission.flight.path.FlightPathPoint;
import edu.rit.se.sars.mission.search.decompose.PolygonDecomposer;
import edu.rit.se.sars.mission.search.partition.PointPartitionAlgorithm;
import edu.rit.se.sars.mission.search.partition.exception.NoSolutionException;
import edu.rit.se.sars.mission.search.routing.RoutingAlgorithm;
import edu.rit.se.sars.mission.target.detection.LocalTargetDetection;
import edu.rit.se.sars.mission.target.detection.TargetIdentifier;
import edu.rit.se.sars.mission.target.tracking.TrackingTaskCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SwarmCoordinator<N extends NetworkEntity> implements MessageHandler<SwarmMessageEnvelope>, Observer {

    private static final Logger logger = LoggerFactory.getLogger(SwarmCoordinator.class);

    private static final int INBOUND_CACHE_SIZE = 1000;

    private final SwarmDeliveryHandler<N> deliveryHandler;
    private final NetworkEntityRegistry<N> entityRegistry;
    private final SwarmStatusMonitor<N> swarmStatusMonitor;
    private final PolygonDecomposer polygonDecomposer;
    private final PointPartitionAlgorithm partitionAlgorithm;
    private final RoutingAlgorithm routingAlgorithm;
    private final FlightPathController flightPathController;
    private final TrackingTaskCalculator trackingTaskCalculator;

    private final Map<UUID, CountDownLatch> pendingLocations = new HashMap<>();
    private final Map<UUID, GeoLocation3D> locations = new HashMap<>();
    private final Set<UUID> inboundMessageUUIDCache;

    private final Thread flightPathControllerThread;

    // Consensus and other coordination state
    private final N self;
    private N leader = null;
    private Mission mission = null;
    private CoverageState coverageState = null;
    private List<N> activeDrones;
    private boolean returningHome = false;
    private final Map<UUID, PointTask> focusCandidates = new HashMap<>();

    public SwarmCoordinator(
        SwarmDeliveryHandler<N> deliveryHandler,
        NetworkEntityRegistry<N> entityRegistry,
        SwarmStatusMonitor<N> swarmStatusMonitor,
        PolygonDecomposer polygonDecomposer,
        PointPartitionAlgorithm partitionAlgorithm,
        RoutingAlgorithm routingAlgorithm,
        FlightPathController flightPathController,
        TargetIdentifier targetIdentifier,
        TrackingTaskCalculator trackingTaskCalculator
    ) {
        this.deliveryHandler = deliveryHandler;
        this.entityRegistry = entityRegistry;
        this.swarmStatusMonitor = swarmStatusMonitor;
        this.polygonDecomposer = polygonDecomposer;
        this.partitionAlgorithm = partitionAlgorithm;
        this.routingAlgorithm = routingAlgorithm;
        this.flightPathController = flightPathController;
        this.trackingTaskCalculator = trackingTaskCalculator;

        this.self = this.entityRegistry.getSelf();
        this.flightPathControllerThread = new Thread(flightPathController);

        flightPathController.addObserver(this);
        swarmStatusMonitor.addObserver(this);
        targetIdentifier.addObserver(this);

        this.inboundMessageUUIDCache = Collections.newSetFromMap(new LinkedHashMap<UUID, Boolean>(){
            protected boolean removeEldestEntry(Map.Entry<UUID, Boolean> eldest) {
            return size() > INBOUND_CACHE_SIZE;
            }
        });
    }

    /**
     * Start mission
     * @param message Mission parameters
     */
    private void startMission(MissionDefinitionMessage message) {
        this.mission = message.getMission();
        logger.debug("Starting mission");

        // Register network entities
        this.mission.getNetworkEntities().forEach(
            entity -> entityRegistry.addEntity((N) entity)
        );

        this.activeDrones = new ArrayList<>(entityRegistry.getAllEntitiesOfType(NetworkEntityType.DRONE));

        this.leader = this.getLeader(this.activeDrones);
        logger.debug("Found leader: {}", leader);

        if (this.leader.equals(this.self)) {
            logger.debug("I'm the leader!");

            List<PointTask> allPoints = this.polygonDecomposer.decompose(
                this.mission.getArea(),
                this.mission.getMinAltitudeMeters(),
                this.flightPathController.getDrone().getCameraProperties()
            ).stream().map(p -> new PointTask(
                p,
                RoutingAlgorithm.defaultGimbalOrientation,
                Optional.empty(),
                1.0
            )).collect(Collectors.toList());
            Map<PointTask, Integer> pointIDMap = new HashMap<>();
            for (int pointID = 0; pointID < allPoints.size(); pointID++) {
                pointIDMap.put(allPoints.get(pointID), pointID);
            }

            logger.debug("Total number of points: {}", allPoints.size());

            List<PointAssignment> pointAssignments = this.getPointAssignments(this.activeDrones, pointIDMap);
            this.distributeCoverage(this.activeDrones, pointAssignments);
        }
    }

    /**
     * Get location of another drone in the swarm
     * @param drone Drone to get location of
     * @return Drone's location
     */
    private GeoLocation3D getLocation(N drone) {
        try {
            if (drone.equals(this.self)) {
                logger.debug("Getting my own location...");
                return this.flightPathController.getDrone().getLocation();
            } else {
                logger.debug("Getting location of {}", drone);

                CountDownLatch countdownLatch = new CountDownLatch(1);

                this.pendingLocations.put(drone.getUuid(), countdownLatch);

                logger.debug("Waiting for location of {}", drone.getUuid());
                deliveryHandler.sendMessage(new LocationQueryMessage(), drone);

                try {
                    countdownLatch.await();
                } catch (InterruptedException e) {
                    this.pendingLocations.remove(drone.getUuid());
                }
                logger.debug("Got location of {}", drone);
            }

            return locations.remove(drone.getUuid());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Determine which drone is the leader
     * @param drones Drones to get leader of
     * @return Leader of drones
     */
    private N getLeader(List<N> drones) {
        N leader = this.self;

        for (N drone : drones) {
            if (drone.getUuid().compareTo(leader.getUuid()) > 0) {
                leader = drone;
            }
        }

        return leader;
    }

    /**
     * Process new coverage state
     * A new flight path will be calculated and executed from the remaining points for this drone
     * @param coverageState Coverage state to process
     */
    private void handleCoverageState(CoverageState coverageState) {
        logger.debug("Got coverage state");
        this.coverageState = coverageState;

        FlightPath flightPath = this.getFlightPath(coverageState);
        this.flightPathController.setFlightPath(flightPath);

        // Start flight if not already running
        if (!this.flightPathControllerThread.isAlive()) {
            this.flightPathControllerThread.start();
        }

        this.swarmStatusMonitor.setEntities(
            this.activeDrones.stream()
                .filter(drone -> !drone.equals(this.self))
                .collect(Collectors.toSet())
        );
    }

    /**
     * Remove completed point from coverage state
     * @param pointCompletedMessage Message containing point to remove
     */
    private void handlePointCompletedMessage(PointCompletedMessage pointCompletedMessage) {
        logger.debug("Got point completed message");

        this.coverageState.getRemainingPointAssignments().removeIf(
            pointAssignment -> pointAssignment.getPointId() == pointCompletedMessage.getPointID()
        );
    }

    /**
     * Construct a flight path for this drone based on the remaining points in the current coverage state
     * @param coverageState Coverage state to retrieve points from
     * @return Flight path through remaining points
     */
    private FlightPath getFlightPath(CoverageState coverageState) {
        double altitudeMeters = coverageState.getAltitudeAssignments().get(this.self.getUuid());

        GeoLocation2D droneLocation = new GeoLocation2D(this.flightPathController.getDrone().getLocation());

        List<PointTask> points = coverageState.getRemainingPointAssignments().stream()
            .filter(pointAssignment ->
                pointAssignment.getDroneUUID().equals(this.self.getUuid())
            ).map(PointAssignment::getPointTask)
            .collect(Collectors.toList());

        logger.debug("Calculating flight path for {} points", points.size());

        return this.routingAlgorithm.getPath(
            droneLocation,
            this.flightPathController.getHomeLocation(),
            points,
            altitudeMeters
        );
    }

    /**
     * Remove drone from swarm. It will no longer be assigned points and its remaining points will be reallocated.
     * @param drone Drone entity to remove
     */
    private void removeDrone(N drone) {
        logger.debug("Removing drone {}", drone);

        // Stop trying to deliver messages to drone
        this.deliveryHandler.cancelResends(drone);

        if (!this.activeDrones.remove(drone)) {
            // Drone already removed (multiple other drones will signal to remove it, only need to do so once)
            return;
        }

        if (drone.equals(this.leader)) {
            // Removed drone was the leader, determine which drone is next leader
            this.leader = getLeader(this.activeDrones);
        }

        if (this.leader.equals(this.self)) {
            // I'm the leader, responsible for re-allocating removed drone's points
            Map<PointTask, Integer> remainingPoints = this.coverageState.getRemainingPointAssignments().stream()
                .collect(Collectors.toMap(
                    PointAssignment::getPointTask, PointAssignment::getPointId
                )
            );

            List<PointAssignment> newPointAssignments = this.getPointAssignments(this.activeDrones, remainingPoints);
            this.distributeCoverage(this.activeDrones, newPointAssignments);
        }
    }

    /**
     * Remove completed point from remaining points in coverage state
     * @param flightPathPoint Completed point to remove
     */
    private void removePoint(FlightPathPoint flightPathPoint) {
        logger.debug("Remaining points: {}", coverageState.getRemainingPointAssignments());
        logger.debug("Removing point: {}", flightPathPoint);

        // TODO: should have ID part of flight path point somehow... this isn't very clean
        Optional<PointAssignment> pointAssignment = this.coverageState.getRemainingPointAssignments().stream().filter(
            p -> p.getPointTask().getLocation().equals(new GeoLocation2D(flightPathPoint.getLocation()))
        ).findFirst();

        if (pointAssignment.isPresent()) {
            logger.debug("Removing completed point assignment");

            this.coverageState.getRemainingPointAssignments().remove(pointAssignment.get());
            this.deliveryHandler.sendMessage(new PointCompletedMessage(pointAssignment.get().getPointId()));
        } else {
            // This was a non-task point (such as taking off to assigned altitude), don't need to tell anyone about it
            logger.debug("Point completed but not assigned one");
        }
    }

    /**
     * Craft coverage state and distribute to drones
     * @param drones Drones to send coverage state to
     * @param pointAssignments Point assignments to create coverage state from
     */
    private void distributeCoverage(List<N> drones, List<PointAssignment> pointAssignments) {
        CoverageState coverageState = new CoverageState(
            0,
            pointAssignments,
            IntStream.range(0, drones.size()).boxed().collect(Collectors.toMap(
                droneIdx -> drones.get(droneIdx).getUuid(),
                droneIdx -> this.mission.getMinAltitudeMeters() + (droneIdx * this.mission.getAltitudeSeparationMeters())
            ))
        );

        for (N drone : drones) {
            if (!drone.equals(self)) {
                deliveryHandler.sendMessage(
                    new CoverageStateMessage(coverageState),
                    drone
                );
            }
        }

        // Sent new coverage state to everyone else, now needs to be handled locally
        this.handleCoverageState(coverageState);
    }

    /**
     * Fairly allocate point tasks amongst the drone swarm
     * @param drones Drones to allocate tasks to
     * @param points Map of point tasks to IDs to allocate
     * @return List of allocated point assignments
     */
    private List<PointAssignment> getPointAssignments(List<N> drones, Map<PointTask, Integer> points) {
        try {
            Map<GeoLocation2D, PointTask> pointTaskMap = new HashMap<>();
            for (PointTask point : points.keySet()) {
                pointTaskMap.put(point.getLocation(), point);
            }

            List<List<GeoLocation2D>> partitionedPoints = this.partitionAlgorithm.partition(
                new LinkedList<>(pointTaskMap.keySet()),
                drones.stream().map(drone ->
                    new GeoLocation2D(this.getLocation(drone))
                ).collect(Collectors.toList())
            );

            List<PointAssignment> pointAssignments = new LinkedList<>();
            for (int droneIdx = 0; droneIdx < partitionedPoints.size(); droneIdx++) {
                for (int dronePointIdx = 0; dronePointIdx < partitionedPoints.get(droneIdx).size(); dronePointIdx++) {
                    GeoLocation2D point = partitionedPoints.get(droneIdx).get(dronePointIdx);

                    PointTask task = pointTaskMap.get(point);

                    pointAssignments.add(
                        new PointAssignment(
                            drones.get(droneIdx).getUuid(),
                            points.get(task),
                            task
                        )
                    );
                }
            }

            return pointAssignments;
        } catch (NoSolutionException e) {
            logger.error("Failed to reach distribution solution", e);

            return new LinkedList<>();
        }
    }

    /**
     * Handle request to allocate focus task
     * @param focusDetectionMessage Message requesting focus on a detection
     */
    private void handleFocusDetectionMessage(FocusDetectionMessage focusDetectionMessage) {
        if (this.leader.equals(this.self)) {
            PointTask pointTask = this.focusCandidates.get(focusDetectionMessage.getDetectionUUID());
            Map<PointTask, Integer> remainingPoints = this.coverageState.getRemainingPointAssignments().stream().collect(Collectors.toMap(
                PointAssignment::getPointTask, PointAssignment::getPointId
            ));
            remainingPoints.put(pointTask, Math.abs(new Random().nextInt()));

            List<PointAssignment> newPointAssignments = this.getPointAssignments(this.activeDrones, remainingPoints);
            this.distributeCoverage(this.activeDrones, newPointAssignments);
        }
    }

    /**
     * Return to home point and land
     * Inform other drones to remove this drone from the swarm - will no longer accept points
     */
    public void returnHome() {
        if (!flightPathController.isInAir() || this.returningHome) {
            // Already landed or returning home, no need to do anything
            return;
        }

        this.activeDrones.stream()
            .filter(drone -> !drone.equals(this.self))
            .forEach(drone ->
                this.deliveryHandler.sendMessage(new RemoveDroneMessage(this.self), drone)
            );

        double altitudeMeters = coverageState.getAltitudeAssignments().get(this.self.getUuid());

        FlightPath returnHomePath = new FlightPath(
            Collections.singletonList(
                new FlightPathPoint(
                    new GeoLocation3D(this.flightPathController.getHomeLocation(), altitudeMeters),
                    new Orientation(0, 0, 0),
                    Optional.empty(),
                    1.0
                )
            )
        );
        this.flightPathController.setFlightPath(returnHomePath);
        this.returningHome = true;
    }

    /**
     * Process local detection of target, notify swarm and operator if needed
     * @param targetDetection Detection to process
     */
    private void handleTargetDetection(LocalTargetDetection targetDetection) {
        // Only send detection if actually in flight - stream + CV is active before takeoff
        logger.debug("Handling target detection");
        if (flightPathController.isInAir()) {
            logger.debug("In air, processing detection!");

            try {
                List<TargetDetectionMessage> messages = TargetDetectionMessage.fromLocalDetection(targetDetection);
                messages.forEach(message -> this.deliveryHandler.sendMessage(message, NetworkEntityType.OPERATOR));
            } catch (IOException e) {
                logger.error("Could not convert detection for transfer", e);
            }

            Drone drone = this.flightPathController.getDrone();
            PointTask trackingTask = trackingTaskCalculator.getTrackingTask(
                drone.getLocation(),
                drone.getOrientation(),
                // TODO: add gimbal orientation retrieval to HW interface?
                flightPathController.getCurrentPoint().getGimbalOrientation(),
                drone.getCameraProperties(),
                targetDetection.getBoundingBox()
            );

            this.focusCandidates.put(targetDetection.getId(), trackingTask);
            this.deliveryHandler.sendMessage(
                new FocusDetectionCandidateMessage(targetDetection.getId(), trackingTask),
                NetworkEntityType.DRONE
            );
        }
    }

    @Override
    public void handleMessage(SwarmMessageEnvelope envelope) {
        if (!this.inboundMessageUUIDCache.contains(envelope.getUuid())) {
            this.inboundMessageUUIDCache.add(envelope.getUuid());
        } else {
            // Message already handled
            return;
        }

        SwarmMessage message = envelope.getMessage();
        logger.debug("Handling {}", message);

        if (message instanceof MissionDefinitionMessage) {
            this.startMission((MissionDefinitionMessage) message);
        } else if (message instanceof CoverageStateMessage) {
            CoverageStateMessage coverageStateMessage = (CoverageStateMessage) message;

            this.handleCoverageState(coverageStateMessage.getCoverageState());
        } else if (message instanceof LocationQueryMessage) {
            logger.debug("Got location query message");

            LocationStatusMessage response = new LocationStatusMessage(
                this.flightPathController.getDrone().getLocation()
            );
            logger.debug("Sending location response: {}", response);

            this.deliveryHandler.sendMessage(response, (N) envelope.getFromEntity());
        } else if (message instanceof LocationStatusMessage) {
            logger.debug("Got location response message from {}", envelope.getFromEntity().getUuid());

            LocationStatusMessage locationStatusMessage = (LocationStatusMessage) message;

            this.locations.put(envelope.getFromEntity().getUuid(), locationStatusMessage.getLocation());

            CountDownLatch countDownLatch = this.pendingLocations.remove(envelope.getFromEntity().getUuid());
            countDownLatch.countDown();
        } else if (message instanceof PointCompletedMessage) {
            PointCompletedMessage pointCompletedMessage = (PointCompletedMessage) message;

            this.handlePointCompletedMessage(pointCompletedMessage);
        } else if (message instanceof FocusDetectionCandidateMessage) {
            logger.debug("Got focus detection candidate message");

            FocusDetectionCandidateMessage focusDetectionCandidateMessage = (FocusDetectionCandidateMessage) message;
            this.focusCandidates.put(
                    focusDetectionCandidateMessage.getDetectionUUID(),
                    focusDetectionCandidateMessage.getPointTask()
            );
        } else if (message instanceof FocusDetectionMessage) {
            logger.debug("Got focus detection message");
            FocusDetectionMessage focusDetectionMessage = (FocusDetectionMessage) message;

            this.handleFocusDetectionMessage(focusDetectionMessage);
        } else if (message instanceof ReturnHomeMessage) {
            this.returnHome();
        } else if (message instanceof RemoveDroneMessage) {
            RemoveDroneMessage removeDroneMessage = (RemoveDroneMessage) message;

            this.removeDrone((N) removeDroneMessage.getDrone());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        logger.debug("Got update {}", arg);
        if (arg instanceof NetworkEntity) {
            N timeoutEntity = (N) arg;

            this.removeDrone(timeoutEntity);
        } else if (arg instanceof FlightPathPoint) {
            FlightPathPoint point = (FlightPathPoint) arg;

            this.removePoint(point);
        } else if (arg instanceof LocalTargetDetection) {
            this.handleTargetDetection((LocalTargetDetection) arg);
        }
    }
}
