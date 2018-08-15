package it.polito.gruppo2.mamk.lab3.services.archive;

import it.polito.gruppo2.mamk.lab3.models.ApproxArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealPosition;

import java.util.List;

public interface ArchiveService {
    /**
     * Gets the approximated representations.
     *
     * @return a pair of timestamps and positions matching the polygon and timestamp
     */
    List<ApproxArchive> getApproximated(String area, Long from, Long to);


    /**
     * Uploades a bunch of positions. It generates the double representation.
     */
    void uploadPositions(List<RealPosition> positions);
}
