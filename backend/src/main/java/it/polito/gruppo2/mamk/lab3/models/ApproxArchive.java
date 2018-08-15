package it.polito.gruppo2.mamk.lab3.models;

import it.polito.gruppo2.mamk.lab3.persistence.approxarchive.ApproxPositionArchive;
import it.polito.gruppo2.mamk.lab3.persistence.approxarchive.ApproxTimestampArchive;

import java.util.Objects;

/**
 * This class is used by the controller to return a couple of approximated representations (timestamp + positions)
 */
public class ApproxArchive {

    String realArchiveId; // Pointer to the real archive

    Integer realPositions; // The number of positions that correspond to that archive, before the shrinking.

    String ownerUsername; // User that uploaded the original archive

    ApproxPositionArchive approxPositionArchive;

    ApproxTimestampArchive approxTimestampArchive;

    public ApproxArchive() {
    }

    public ApproxArchive(String realArchiveId, Integer realPositions, String ownerUsername, ApproxPositionArchive approxPositionArchive, ApproxTimestampArchive approxTimestampArchive) {
        this.realArchiveId = realArchiveId;
        this.realPositions = realPositions;
        this.ownerUsername = ownerUsername;
        this.approxPositionArchive = approxPositionArchive;
        this.approxTimestampArchive = approxTimestampArchive;
    }

    public Integer getRealPositions() {
        return realPositions;
    }

    public void setRealPositions(Integer realPositions) {
        this.realPositions = realPositions;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public ApproxPositionArchive getApproxPositionArchive() {
        return approxPositionArchive;
    }

    public void setApproxPositionArchive(ApproxPositionArchive approxPositionArchive) {
        this.approxPositionArchive = approxPositionArchive;
    }

    public ApproxTimestampArchive getApproxTimestampArchive() {
        return approxTimestampArchive;
    }

    public void setApproxTimestampArchive(ApproxTimestampArchive approxTimestampArchive) {
        this.approxTimestampArchive = approxTimestampArchive;
    }

    public String getRealArchiveId() {
        return realArchiveId;
    }

    public void setRealArchiveId(String realArchiveId) {
        this.realArchiveId = realArchiveId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApproxArchive)) return false;
        ApproxArchive that = (ApproxArchive) o;
        return Objects.equals(getRealArchiveId(), that.getRealArchiveId()) &&
                Objects.equals(getRealPositions(), that.getRealPositions()) &&
                Objects.equals(getOwnerUsername(), that.getOwnerUsername()) &&
                Objects.equals(getApproxPositionArchive(), that.getApproxPositionArchive()) &&
                Objects.equals(getApproxTimestampArchive(), that.getApproxTimestampArchive());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getRealArchiveId(), getRealPositions(), getOwnerUsername(), getApproxPositionArchive(), getApproxTimestampArchive());
    }
}
