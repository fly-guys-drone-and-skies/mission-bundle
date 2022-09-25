package edu.rit.se.sars.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GeoPolygon implements ProtobufSerializable<edu.rit.se.sars.communication.proto.common.domain.GeoPolygon> {

    private final List<GeoLocation2D> vertices;

    @JsonCreator
    public GeoPolygon(@JsonProperty("vertices") List<GeoLocation2D> vertices) {
        this.vertices = vertices;
    }

    public GeoPolygon(edu.rit.se.sars.communication.proto.common.domain.GeoPolygon geoPolygon) {
        this(
            geoPolygon.getVerticesList().stream()
                .map(GeoLocation2D::new)
                .collect(Collectors.toList())
        );
    }

    /**
     * @return Rectangle bounding this polygon
     */
    public GeoRectangle getBoundingRectangle() {
        GeoLocation2D firstPoint = this.vertices.get(0);

        // Bounding rectangle
        double x1 = firstPoint.getLongitude(), x2 = firstPoint.getLongitude(); // min and max X
        double y1 = firstPoint.getLatitude(), y2 = firstPoint.getLatitude(); // min and max Y

        for (int i = 1; i < this.vertices.size(); i++) {
            GeoLocation2D currPoint = this.vertices.get(i);
            x1 = Math.min(currPoint.getLongitude(), x1);
            y1 = Math.min(currPoint.getLatitude(), y1);
            x2 = Math.max(currPoint.getLongitude(), x2);
            y2 = Math.max(currPoint.getLatitude(), y2);
        }

        GeoLocation2D lowerLeft = new GeoLocation2D(y1, x1);
        GeoLocation2D upperRight = new GeoLocation2D(y2, x2);

        return new GeoRectangle(lowerLeft, upperRight);
    }


    /**
     * Check if point is inside polygon
     * @param point Point to check
     * @return True if point is in polygon, false otherwise
     */
    public boolean contains(GeoLocation2D point) {
        // https://web.archive.org/web/20161108113341/https://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        boolean result = false;
        int i, j;
        for (i = 0, j = this.vertices.size() - 1; i < vertices.size(); j = i++) {
            GeoLocation2D iPoint = vertices.get(i);
            GeoLocation2D jPoint = vertices.get(j);

            if ((iPoint.latitude > point.latitude) != (jPoint.latitude > point.latitude) &&
                (point.longitude <
                    (jPoint.longitude - iPoint.longitude) *
                    (point.latitude - iPoint.latitude) /
                    (jPoint.latitude - iPoint.latitude) +
                    iPoint.longitude
                )
            ) {
                result = !result;
            }
        }
        return result;
    }

    @Override
    public edu.rit.se.sars.communication.proto.common.domain.GeoPolygon toProtobuf() {
        return edu.rit.se.sars.communication.proto.common.domain.GeoPolygon.newBuilder()
            .addAllVertices(
                this.vertices.stream().map(
                    GeoLocation2D::toProtobuf
                ).collect(Collectors.toList())
            ).build();
    }
}
