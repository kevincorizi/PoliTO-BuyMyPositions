package it.polito.gruppo2.mamk.lab3.persistence.approxarchive;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApproxTimestampArchiveRepository extends MongoRepository<ApproxTimestampArchive, String> {
    ApproxTimestampArchive findApproxTimestampArchiveByOriginalArchiveIs(String id);

    void removeByOriginalArchive(String id);

}
