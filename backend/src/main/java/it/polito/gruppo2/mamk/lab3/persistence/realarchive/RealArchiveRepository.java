package it.polito.gruppo2.mamk.lab3.persistence.realarchive;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.HashSet;
import java.util.List;

public interface RealArchiveRepository extends MongoRepository<RealArchive, String>, CustomRealArchiveRepository {

    List<RealArchive> getRealArchivesByOwnerUsernameAndCanBeBoughtIsTrue(String username);

    RealArchive getRealArchivesByIdAndOwnerUsernameAndCanBeBoughtIsTrue(String id, String username);

    List<RealArchive> getRealArchivesByIdIn(HashSet<String> ids);

    RealArchive getRealArchivesById(String id);

    RealArchive findTopByOwnerUsernameAndCanBeBoughtOrderByUploadtimeDesc(String ownerUsername, Boolean canBeBought);

    void removeById(String id);

}
