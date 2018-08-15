package it.polito.gruppo2.mamk.lab3.services.archive;

import it.polito.gruppo2.mamk.lab3.exceptions.BadRequestException;
import it.polito.gruppo2.mamk.lab3.exceptions.InvalidPositionException;
import it.polito.gruppo2.mamk.lab3.models.ApproxArchive;
import it.polito.gruppo2.mamk.lab3.persistence.approxarchive.*;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchive;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealArchiveRepository;
import it.polito.gruppo2.mamk.lab3.persistence.realarchive.RealPosition;
import it.polito.gruppo2.mamk.lab3.persistence.user.UsersRepository;
import it.polito.gruppo2.mamk.lab3.services.BasicServiceImpl;
import it.polito.gruppo2.mamk.lab3.utils.PositionsTimestampComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ArchiveServiceImpl extends BasicServiceImpl implements ArchiveService {

    @Autowired
    ApproxPositionArchiveRepository approxPositionArchiveRepository;

    @Autowired
    ApproxTimestampArchiveRepository approxTimestampArchiveRepository;

    @Autowired
    RealArchiveRepository realArchiveRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PositionsTimestampComparator positionsComparator;


    @Override
    @PreAuthorize("hasRole('USER')")
    public List<ApproxArchive> getApproximated(String area, Long from, Long to) {
        validateCustomerRequest(from, to);

        GeoJsonPolygon requestedPolygon = parseGeoJsonString(area);
        List<RealArchive> realArchives;
        try {
            realArchives = realArchiveRepository
                    .findArchivesIdByPositionInAndTimestampBetweenAndCanBeBought(getUsername(), requestedPolygon, from, to);
        } catch (Exception e) {
            // Let the db handle the polygon validation
            throw new BadRequestException("Polygon not valid");
        }
        List<ApproxArchive> approxArchives = new ArrayList<ApproxArchive>();

        ApproxArchive approxArchive;
        for (RealArchive archive : realArchives) {
            ApproxTimestampArchive approxTimestampArchive = approxTimestampArchiveRepository.findApproxTimestampArchiveByOriginalArchiveIs(archive.getId());
            ApproxPositionArchive approxPositionArchive = approxPositionArchiveRepository.findApproxPositionArchiveByOriginalArchiveIs(archive.getId());
            approxArchive = new ApproxArchive(archive.getId(), archive.getPositions().size(),
                    archive.getOwnerUsername(),
                    approxPositionArchive,
                    approxTimestampArchive);
            approxArchives.add(approxArchive);
        }
        return approxArchives;
    }

    @Override
    @Transactional // We want the three representation to be stored atomically
    @PreAuthorize("hasRole('USER')")
    public void uploadPositions(List<RealPosition> positions) {
        if (positions.size() == 0) {
            throw new BadRequestException("Empty position list");
        }

        String username = BasicServiceImpl.getUsername();

        // Sorting provided positions upon tstamp in ascending order
        Collections.sort(positions, positionsComparator);

        // Validating positions to enforce speed and timestamp constraints
        validatePositions(positions, username);

        // Storing the real representation
        RealArchive realArchive = new RealArchive();
        realArchive.setPositions(new ArrayList<>(positions));
        realArchive.setOwnerUsername(username);
        realArchive.setUploadtime(new Date().getTime());

        realArchive = realArchiveRepository.insert(realArchive);

        // Creating the approximations
        TreeSet<Long> approxTimestamps = new TreeSet<Long>();
        TreeSet<ApproxPosition> approxPositions = new TreeSet<ApproxPosition>();

        realArchive.getPositions().forEach((position) -> {
            approxTimestamps.add(position.getTimestamp() - (position.getTimestamp() % 60000));
            approxPositions.add(
                    new ApproxPosition(
                            Math.floor(position.getGeoPoint().getY() * 100) / 100,
                            Math.floor(position.getGeoPoint().getX() * 100) / 100
                    )
            );
        });
        //Ordered and duplicates removed by the three set.

        ApproxTimestampArchive approxTimestampArchive = new ApproxTimestampArchive();
        ApproxPositionArchive approxPositionArchive = new ApproxPositionArchive();

        approxPositionArchive.setGeoPoints(approxPositions);
        approxTimestampArchive.setTimestamps(approxTimestamps);

        approxPositionArchive.setOriginalArchive(realArchive.getId());
        approxTimestampArchive.setOriginalArchive(realArchive.getId());

        approxTimestampArchiveRepository.insert(approxTimestampArchive);
        approxPositionArchiveRepository.insert(approxPositionArchive);
    }


    // It is important to avoid that a user who has checked the number of positions for a certain interval
    // does not risk to find more positions than expected in a certain area.
    private void validateCustomerRequest(long from, long to) {
        if (from > new Date().getTime()) {
            throw new BadRequestException("You can't buy positions in the future.");
        } else if (from > to) {
            throw new BadRequestException("Present and future are confused!");
        }
    }


    // Method comparing the last position provided in the last archive uploaded
    // with the newly uploaded (ordered) list of positions to enforce timestamp and speed constraints
    private void validatePositions(List<RealPosition> positions, String username){
        Double speedThreshold = 100.0;
        RealPosition lastValid;
        List<RealPosition> toBeTested;
        RealArchive lastArchive = realArchiveRepository.findTopByOwnerUsernameAndCanBeBoughtOrderByUploadtimeDesc(username, true);

        // Setup +++ Retrieve last valid position for user
        if(lastArchive == null){
            // First post from user <username>
            if(positions.size() == 1){ // Single position provided --> automatically valid
                return;
            }else{ // The last valid position is the firs provided
                lastValid = positions.get(0);
                toBeTested = positions.subList(1, positions.size());
            }
        }else{
            // Retrieve last valid position of last uploaded archive
            List<RealPosition> lastUploadedPos = lastArchive.getPositions();
            lastValid = lastUploadedPos.get(lastUploadedPos.size()-1);
            toBeTested = positions;
        }

        // ++ Actual testing ++ NB: latitude is Y longitude is X
        for (RealPosition current : toBeTested) {
            // Checking constraint on timestamps
            long timeDistance = current.getTimestamp() - lastValid.getTimestamp();
            if (timeDistance > 0) {
                // Checking constraint on speed.
                double physicDistance = distFrom(lastValid.getGeoPoint().getY(), lastValid.getGeoPoint().getX(),
                                                 current.getGeoPoint().getY(), current.getGeoPoint().getX());
                if (physicDistance / timeDistance < speedThreshold) {
                    lastValid = current;
                } else {
                    throw new InvalidPositionException("Speed threshold exceeded!");
                }
            } else {
                throw new InvalidPositionException("Provided timestamp overlapping with previous ones!");
            }
        }

    }

    // Haversine formula for distance computation in meters
    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

}
