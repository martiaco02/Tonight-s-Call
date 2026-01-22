package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Abstract Data Transfer Object representing the base structure for any Organizer entity.
 * <p>
 * This class uses polymorphism to distinguish between an individual {@link OrganizerDTO}
 * and an {@link OrganizationDTO}. The distinction is made via the "type" field.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrganizerDTO.class, name = "ORGANIZER"),
        @JsonSubTypes.Type(value = OrganizationDTO.class, name = "ORGANIZATION")
})
@Schema(
        description = "Base model for organizers. Can be either an individual 'ORGANIZER' or an 'ORGANIZATION'.",
        discriminatorProperty = "type",
        subTypes = {OrganizerDTO.class, OrganizationDTO.class}
)
public abstract class AbstracOrganizerDTO {

    @JsonProperty("_id")
    @Schema(description = "Unique identifier of the entity", example = "65a12b3c4d5e6f7g8h9i0j1k")
    private String id;

    @JsonProperty("type")
    @Schema(
            description = "Discriminator field to identify the type of organizer",
            example = "ORGANIZER",
            allowableValues = {"ORGANIZER", "ORGANIZATION"}
    )
    private String type;

    @JsonProperty("name")
    @Schema(description = "Display name of the organizer or organization", example = "Summer Events Ltd.")
    private String name;

    @JsonProperty("VAT_number")
    @Schema(description = "VAT number for tax purposes", example = "IT12345678901")
    private String vatNumber;

    @JsonProperty("email")
    @Schema(description = "Contact email address", example = "contact@email.com")
    private String email;

    @JsonProperty("events")
    @Schema(description = "List of events managed by this entity")
    private List<EventOrganizationDTO> events;
}

