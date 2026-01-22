package it.unipi.tonightscall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded Entity representing the User's hometown.
 * <p>
 * Contains the name and the geospatial location of the city.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeTown {

    /**
     * The name of the hometown.
     */
    @Field("name")
    private String name;

    /**
     * The geospatial location data.
     * Mapped to the "loc" field in MongoDB.
     */
    @Field("loc")
    private Location location;
}