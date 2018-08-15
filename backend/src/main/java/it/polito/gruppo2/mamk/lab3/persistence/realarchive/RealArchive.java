package it.polito.gruppo2.mamk.lab3.persistence.realarchive;


import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents the model of the archives containing the real positions, i.e. the one uploaded by the users.
 */
@Document()
public class RealArchive {
    @Id
    @GeneratedValue
    private String id;

    private String ownerUsername; // Points to the user that uploaded it

    private boolean canBeBought = true; // true if it has not been deleted

    private Long uploadtime; // When has it been uploaded

    ArrayList<RealPosition> positions;
    private Long timesBought = 0L; // How many times it has been bought

    public RealArchive(String id, String ownerUsername, boolean canBeBought, Long uploadtime, Long timesBought, ArrayList<RealPosition> positions) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.canBeBought = canBeBought;
        this.uploadtime = uploadtime;
        this.timesBought = timesBought;
        this.positions = positions;
    }

    public RealArchive(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public boolean isCanBeBought() {
        return canBeBought;
    }

    public void setCanBeBought(boolean canBeBought) {
        this.canBeBought = canBeBought;
    }

    public Long getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(Long uploadtime) {
        this.uploadtime = uploadtime;
    }

    public Long getTimesBought() {
        return timesBought;
    }

    public void setTimesBought(Long timesBought) {
        this.timesBought = timesBought;
    }

    public ArrayList<RealPosition> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<RealPosition> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealArchive)) return false;
        RealArchive that = (RealArchive) o;
        return isCanBeBought() == that.isCanBeBought() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getOwnerUsername(), that.getOwnerUsername()) &&
                Objects.equals(getUploadtime(), that.getUploadtime()) &&
                Objects.equals(getTimesBought(), that.getTimesBought()) &&
                Objects.equals(getPositions(), that.getPositions());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getOwnerUsername(), isCanBeBought(), getUploadtime(), getTimesBought(), getPositions());
    }
}
