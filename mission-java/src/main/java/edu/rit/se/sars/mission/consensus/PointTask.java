package edu.rit.se.sars.mission.consensus;

import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.Orientation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

/**
 * Distributable search task
 */
@Data
@AllArgsConstructor
public class PointTask implements ProtobufSerializable<edu.rit.se.sars.communication.proto.external.PointTask> {

    private final GeoLocation2D location;
    private final Orientation gimbalOrientation;
    private final Optional<Double> headingDegrees;
    private final double zoomLevel;

    public PointTask(edu.rit.se.sars.communication.proto.external.PointTask message) {
        this.location = new GeoLocation2D(message.getLocation());
        this.gimbalOrientation = new Orientation(message.getGimbalOrientation());
        if (message.hasHeadingDegrees()) {
            this.headingDegrees = Optional.of(message.getHeadingDegrees());
        } else {
            this.headingDegrees = Optional.empty();
        }
        this.zoomLevel = message.getZoomLevel();
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.PointTask toProtobuf() {
        edu.rit.se.sars.communication.proto.external.PointTask.Builder builder =
            edu.rit.se.sars.communication.proto.external.PointTask.newBuilder()
                .setLocation(this.location.toProtobuf())
                .setGimbalOrientation(this.gimbalOrientation.toProtobuf())
                .setZoomLevel(this.zoomLevel);

        this.headingDegrees.ifPresent(builder::setHeadingDegrees);

        return builder.build();
    }
}
