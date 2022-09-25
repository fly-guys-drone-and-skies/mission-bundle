package edu.rit.se.sars.drone;

import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.domain.GeoLocation3D;
import lombok.Data;

// TODO: implementation for SwarmCoordinator to use for retrieving other drone locations
@Data
public class ExternalDroneProxy<N extends NetworkEntity> {

    private final SwarmDeliveryHandler<N> deliveryHandler;
    private final N entity;

    public GeoLocation3D getLocation() {
        // TODO
        return null;
    }
}
