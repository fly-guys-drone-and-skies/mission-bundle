package edu.rit.se.sars.mission.flight.interrupt;

import edu.rit.se.sars.communication.message.MessagePublisher;
import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.internal.event.DroneEvent;
import edu.rit.se.sars.mission.consensus.SwarmCoordinator;
import edu.rit.se.sars.mission.flight.interrupt.module.LowBatteryReturnModule;
import edu.rit.se.sars.mission.flight.interrupt.module.PathInterruptModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PathInterruptController extends MessagePublisher<DroneEvent> implements MessageHandler<DroneEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PathInterruptController.class);

    private final Set<PathInterruptModule> modules;

    public PathInterruptController(SwarmCoordinator<?> swarmCoordinator) {
        this.modules = new HashSet<>(Arrays.asList(
           new LowBatteryReturnModule(swarmCoordinator)
        ));

        this.modules.forEach(this::addSubscriber);
    }

    @Override
    public void handleMessage(DroneEvent message) {
        logger.debug("Got event: {}", message);

        this.publish(message);
    }
}
