package it.polito.gruppo2.mamk.lab3.persistence.approxarchive;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Objects;

/**
 * This model represents an approximated position.
 *
 * This wrapper is needed to implement the Comparable interface.
 */
public class ApproxPosition implements Comparable<ApproxPosition> {

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint geoPoint;

    @JsonCreator
    public ApproxPosition(@JsonProperty(value = "latitude", required = true) double latitude,
                          @JsonProperty(value = "longitude", required = true) double longitude) {

        geoPoint = new GeoJsonPoint(longitude, latitude); // X and Y
    }

    public ApproxPosition() {
    }


    public GeoJsonPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoJsonPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApproxPosition)) return false;
        ApproxPosition that = (ApproxPosition) o;
        return Objects.equals(getGeoPoint(), that.getGeoPoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeoPoint());
    }

    @Override
    public int compareTo(ApproxPosition approxPosition) {
        if (this.getGeoPoint().getX() > approxPosition.getGeoPoint().getX()) {
            return 1;
        } else if (this.getGeoPoint().getX() < approxPosition.getGeoPoint().getX()) {
            return -1;
        } else {
            if (this.getGeoPoint().getY() > approxPosition.getGeoPoint().getY()) {
                return 1;
            } else if (this.getGeoPoint().getY() < approxPosition.getGeoPoint().getY()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
