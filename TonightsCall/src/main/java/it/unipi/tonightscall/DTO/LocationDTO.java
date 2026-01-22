package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing geospatial coordinates.
 * <p>
 * This structure typically follows the GeoJSON format (e.g., used by MongoDB).
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing a geospatial point (GeoJSON format)")
public class LocationDTO {

    /**
     * The type of the geospatial shape.
     * Usually "Point" for a single location.
     */
    @JsonProperty("type")
    @Schema(description = "Type of the geospatial shape", example = "Point")
    private String type;

    /**
     * The coordinates of the location.
     * <p>
     * <strong>Note:</strong> In GeoJSON/MongoDB, the order is typically [Longitude, Latitude].
     * </p>
     */
    @JsonProperty("coordinates")
    @Schema(
            description = "List of coordinates. Standard format is [Longitude, Latitude]",
            example = "[10.401689, 43.71669]"
    )
    private List<Double> coordinates;

}
