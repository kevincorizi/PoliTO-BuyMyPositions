package it.polito.gruppo2.mamk.lab3.persistence.realarchive;

import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.List;

public interface CustomRealArchiveRepository {

    List<RealArchive> findArchivesIdByPositionInAndTimestampBetweenAndCanBeBought(String username,
                                                                                  GeoJsonPolygon area, Long from, Long to);

}
