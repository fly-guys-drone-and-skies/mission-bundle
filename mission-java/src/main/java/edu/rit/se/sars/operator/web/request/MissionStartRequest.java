package edu.rit.se.sars.operator.web.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.rit.se.sars.domain.GeoPolygon;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Mission parameters defined by operator
 */
@Data
public class MissionStartRequest {
    private final GeoPolygon area;
    private final double minAltitudeMeters;
    private final double altitudeSeparationMeters;
    private final List<InetSocketAddress> drones;

    /**
     * @param area Mission area
     * @param minAltitudeMeters Minimum operating altitude
     * @param altitudeSeparationMeters Altitude separation between drones
     * @param drones Drones to use in mission
     */
    @JsonCreator
    public MissionStartRequest(
        @JsonProperty("area") GeoPolygon area,
        @JsonProperty("minAltitudeMeters") double minAltitudeMeters,
        @JsonProperty("altitudeSeparationMeters") double altitudeSeparationMeters,
        @JsonProperty("drones") List<InetSocketAddress> drones
    ) {
        this.area = area;
        this.minAltitudeMeters = minAltitudeMeters;
        this.altitudeSeparationMeters = altitudeSeparationMeters;
        this.drones = drones;
    }
}
