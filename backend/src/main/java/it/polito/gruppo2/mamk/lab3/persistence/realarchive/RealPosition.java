package it.polito.gruppo2.mamk.lab3.persistence.realarchive;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polito.gruppo2.mamk.lab3.exceptions.InvalidPositionException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Date;
import java.util.Objects;

/**
 * This model represents a position as it is uploaded by the user.
 * It is not annotated as document because it is never stored alone in the database, but only as a RealArchive subdocument.
 */
public class RealPosition {

    //TODO remove this comment: qui avevo l'ID quando ho provato la query geografica nested

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint geoPoint;

    private Long timestamp;

    @JsonCreator
    public RealPosition(@JsonProperty(value = "latitude", required = true) double latitude,
                    @JsonProperty(value = "longitude", required = true) double longitude,
                    @JsonProperty(value = "timestamp", required = true) Long timestamp) {

        if (timestamp > new Date().getTime()) {
            throw new InvalidPositionException("It is not allowed to post a position in the future!");
        }
        this.timestamp = timestamp;

        if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
            throw new InvalidPositionException("Invalid coordinates!");
        }
        geoPoint = new GeoJsonPoint(longitude, latitude); // X and Y
    }

    public RealPosition(){}


    public GeoJsonPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoJsonPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealPosition)) return false;
        RealPosition that = (RealPosition) o;
        return Objects.equals(getGeoPoint(), that.getGeoPoint()) &&
                Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeoPoint(), getTimestamp());
    }
}
