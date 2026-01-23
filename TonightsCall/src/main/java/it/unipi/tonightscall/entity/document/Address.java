package it.unipi.tonightscall.entity.document;

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
public class Address {

    /**
     * The name of the hometown.
     */
    @Field("city_name")
    private String cityName;

    /**
     * The full address
     */
    @Field("full_address")
    private String fullAddress;

    /**
     * The geospatial location data.
     * Mapped to the "loc" field in MongoDB.
     */
    @Field("loc")
    private Location location;
}