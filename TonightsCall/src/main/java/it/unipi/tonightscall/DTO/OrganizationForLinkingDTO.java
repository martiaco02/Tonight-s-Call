package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO used for linking an organization to an individual organizer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reference to an Organization")
public class OrganizationForLinkingDTO {

    /**
     * Unique identifier of the organization.
     */
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the organization", example = "65a12b3c4d5e6f7g8h9i0j1k")
    private String id;

    /**
     * Name of the organization.
     */
    @JsonProperty("name")
    @Schema(description = "Name of the organization", example = "Music Festivals Inc.")
    private String name;
}
