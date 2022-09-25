package edu.rit.se.sars.mission.flight.interrupt.module;

import edu.rit.se.sars.communication.message.internal.event.BatteryStatusEvent;
import edu.rit.se.sars.communication.message.internal.event.DroneEvent;
import edu.rit.se.sars.mission.consensus.SwarmCoordinator;

public class LowBatteryReturnModule extends PathInterruptModule {

    // TODO: Should pull from a config?
    private static final double BATTERY_THRESHOLD_PCT = 10;

    public LowBatteryReturnModule(SwarmCoordinator<?> swarmCoordinator) {
        super(swarmCoordinator);
    }

    @Override
    public void handleMessage(DroneEvent message) {
        if (message instanceof BatteryStatusEvent) {
            BatteryStatusEvent batteryStatusEvent = (BatteryStatusEvent) message;

            if (batteryStatusEvent.getPercentageRemaining() < BATTERY_THRESHOLD_PCT) {
                // Battery is insufficient to continue mission - instruct drone to return home
                swarmCoordinator.returnHome();
            }
        }
    }
}
