package it.polito.gruppo2.mamk.lab3.persistence.realarchive;

import it.polito.gruppo2.mamk.lab3.persistence.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.List;

public class CustomRealArchiveRepositoryImpl implements CustomRealArchiveRepository{

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UsersRepository usersRepository;

    /**
     * @param area polygon
     * @param from starting timestamp
     * @param to   ending timestamp
     * @return the archives that contains at least one position in the polygon, that have not been updated
     * by the user and that are not already its property
     */
    public List<RealArchive> findArchivesIdByPositionInAndTimestampBetweenAndCanBeBought(String username, GeoJsonPolygon area, Long from, Long to) {
        HashSet<String> boughtArchives = usersRepository.findUserByUsername(username).getBoughtArchives();

        Query query = new Query(Criteria.where("positions.geoPoint").within(area));
        query.addCriteria(Criteria.where("positions.timestamp").gt(from).andOperator(Criteria.where("positions.timestamp").lte(to)));
        query.addCriteria(Criteria.where("canBeBought").is(true));
        query.addCriteria(Criteria.where("ownerUsername").ne(username));

        query.addCriteria(Criteria.where("id").nin(boughtArchives));
        return mongoTemplate.find(query, RealArchive.class);
    }
}
