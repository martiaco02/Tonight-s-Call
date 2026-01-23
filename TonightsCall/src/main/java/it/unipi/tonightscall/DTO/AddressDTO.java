package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Data Transfer Object (DTO) representing the User's hometown information.
 * <p>
 * This class encapsulates the name of the city and its geospatial location.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing the user's hometown location")
public class AddressDTO {

    /**
     * The name of the city or town.
     */
    @JsonProperty("city_name")
    @Schema(description = "Name of the user's Home town", example = "Porcari")
    private String cityName;

    /**
     * The complete address
     */
    @JsonProperty("full_address")
    @Schema(description = "Full address of the user's Home town", example = "Via Leccio 38, Porcari, Lucca")
    private String fullAddress;

    /**
     * The geospatial coordinates of the hometown.
     */
    @JsonProperty("loc")
    @Schema(description = "Coordinate of the user's Home town")
    private LocationDTO loc;


}
