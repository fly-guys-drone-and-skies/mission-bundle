package edu.rit.se.sars.domain;

import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Orientation implements ProtobufSerializable<edu.rit.se.sars.communication.proto.common.domain.Orientation> {

    private final double pitchDegrees;
    private final double yawDegrees;
    private final double rollDegrees;

    public Orientation(edu.rit.se.sars.communication.proto.common.domain.Orientation orientation) {
        this(
            orientation.getPitchDegrees(),
            orientation.getYawDegrees(),
            orientation.getRollDegrees()
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.common.domain.Orientation toProtobuf() {
        return edu.rit.se.sars.communication.proto.common.domain.Orientation.newBuilder()
            .setPitchDegrees(this.pitchDegrees)
            .setYawDegrees(this.yawDegrees)
            .setRollDegrees(this.rollDegrees)
            .build();
    }
}
