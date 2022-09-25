package edu.rit.se.sars.mission.flight.interrupt.module;

import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.internal.event.DroneEvent;
import edu.rit.se.sars.mission.consensus.SwarmCoordinator;

/**
 * A module that receives drone events, capable of inducing flight path changes via the swarm coordinator in response
 * to events.
 */
public abstract class PathInterruptModule implements MessageHandler<DroneEvent> {

    protected final SwarmCoordinator<?> swarmCoordinator;

    public PathInterruptModule(SwarmCoordinator<?> swarmCoordinator) {
        this.swarmCoordinator = swarmCoordinator;
    }
}
