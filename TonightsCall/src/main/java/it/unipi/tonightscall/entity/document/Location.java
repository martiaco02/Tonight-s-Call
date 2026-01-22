package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Embedded Entity representing a GeoJSON location point.
 * <p>
 * Compliant with MongoDB's Geospatial queries structure.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    /**
     * The type of the GeoJSON object.
     * Typically "Point".
     */
    @Field("type")
    private String type;

    /**
     * The coordinates of the point.
     * <p>
     * <b>Format:</b> [Longitude, Latitude]
     * </p>
     */
    @Field("coordinates")
    private List<Double> coordinates;
}