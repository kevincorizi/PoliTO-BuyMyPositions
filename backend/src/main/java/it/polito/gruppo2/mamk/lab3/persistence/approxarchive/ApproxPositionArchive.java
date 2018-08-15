package it.polito.gruppo2.mamk.lab3.persistence.approxarchive;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.TreeSet;

@Document()
public class ApproxPositionArchive {

    @Id
    @JsonIgnore
    @GeneratedValue
    private String id;

    @JsonIgnore
    @Indexed
    private String originalArchive; //"Pointer" to the not approximated archive

    private TreeSet<ApproxPosition> geoPoints;

    public ApproxPositionArchive(String originalArchive, TreeSet<ApproxPosition> geoPoints) {
        this.originalArchive = originalArchive;
        this.geoPoints = geoPoints;
    }

    public ApproxPositionArchive() {
        geoPoints = new TreeSet<ApproxPosition>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalArchive() {
        return originalArchive;
    }

    public void setOriginalArchive(String originalArchive) {
        this.originalArchive = originalArchive;
    }

    public TreeSet<ApproxPosition> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(TreeSet<ApproxPosition> geoPoint) {
        this.geoPoints = geoPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApproxPositionArchive)) return false;
        ApproxPositionArchive that = (ApproxPositionArchive) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getOriginalArchive(), that.getOriginalArchive()) &&
                Objects.equals(getGeoPoints(), that.getGeoPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOriginalArchive(), getGeoPoints());
    }
}
