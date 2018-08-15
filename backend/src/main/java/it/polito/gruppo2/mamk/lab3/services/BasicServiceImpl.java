package it.polito.gruppo2.mamk.lab3.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.gruppo2.mamk.lab3.exceptions.BadRequestException;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BasicServiceImpl {
    // The mapper will be used by all services in order to serialize/deserialize positions or polygons
    private final ObjectMapper mapper = getObjectMapper();

    // This function will be invoked only within the context of service methods
    // Access to these methods is allowed only to authenticated users thus we are sure that
    // the Authentication object is not instanceof AnonymousAuthenticationToken
    // The Principal object contains User Authentication data of the currently logged user
    // It is automagically (<3) injected at runtime
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // This module enables serialization/deserialization of GeoJsonObject types
        mapper.registerModule(new GeoJsonModule());
        return mapper;
    }

    public GeoJsonPolygon parseGeoJsonString(String geoJsonString) {
        GeoJsonPolygon result;
        try {
            result = mapper.readValue(geoJsonString, GeoJsonPolygon.class);
        } catch (Exception e) {
            throw new BadRequestException("The provided object is not a valid GeoJsonPolygon!");
        }
        return result;
    }



}
