package it.polito.gruppo2.mamk.lab3.persistence.approxarchive;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApproxPositionArchiveRepository extends MongoRepository<ApproxPositionArchive, String> {

    ApproxPositionArchive findApproxPositionArchiveByOriginalArchiveIs(String id);

    void removeByOriginalArchive(String id);
}
