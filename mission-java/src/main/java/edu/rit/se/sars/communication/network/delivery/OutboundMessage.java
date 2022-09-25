package edu.rit.se.sars.communication.network.delivery;

import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import lombok.Data;

/**
 * Outbound message and intended recipient
 * @param <M> Message type
 * @param <R> Recipient type
 */
@Data
public class OutboundMessage<M, R extends NetworkEntity> {
    private final M message;
    private final R recipient;
}
