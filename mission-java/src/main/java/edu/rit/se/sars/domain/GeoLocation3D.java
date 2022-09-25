package edu.rit.se.sars.domain;

import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GeoLocation3D implements ProtobufSerializable<edu.rit.se.sars.communication.proto.common.domain.GeoLocation3D> {

    private final double latitude;
    private final double longitude;
    private final double altitudeMeters;

    public GeoLocation3D(GeoLocation2D location2D, double altitudeMeters) {
        this(
            location2D.latitude,
            location2D.longitude,
            altitudeMeters
        );
    }

    public GeoLocation3D(edu.rit.se.sars.communication.proto.common.domain.GeoLocation3D location3D) {
        this(
            location3D.getLatitude(),
            location3D.getLongitude(),
            location3D.getAltitudeMeters()
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.common.domain.GeoLocation3D toProtobuf() {
        return edu.rit.se.sars.communication.proto.common.domain.GeoLocation3D.newBuilder()
            .setLatitude(this.latitude)
            .setLongitude(this.longitude)
            .setAltitudeMeters(this.altitudeMeters)
            .build();
    }
}
