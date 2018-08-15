package it.polito.gruppo2.mamk.lab3.persistence.approxarchive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.TreeSet;

@Document()
public class ApproxTimestampArchive {

    @Id
    @GeneratedValue
    @JsonIgnore
    private String id;

    @JsonIgnore
    @Indexed
    private String originalArchive; //"Pointer" to the not approximated archive

    TreeSet<Long> timestamps; // Approximated timestamps

    public ApproxTimestampArchive(String id, String originalArchive, TreeSet<Long> timestamps) {
        this.id = id;
        this.originalArchive = originalArchive;
        this.timestamps = timestamps;
    }


    public ApproxTimestampArchive() {
        this.timestamps = new TreeSet<>();
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

    public TreeSet<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(TreeSet<Long> timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApproxTimestampArchive)) return false;
        ApproxTimestampArchive that = (ApproxTimestampArchive) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getOriginalArchive(), that.getOriginalArchive()) &&
                Objects.equals(getTimestamps(), that.getTimestamps());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getOriginalArchive(), getTimestamps());
    }
}
