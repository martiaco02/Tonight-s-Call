package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

@Data
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
public abstract class AbstracOrganizerDTO {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("VAT_number")
    private String vatNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("events")
    private List<EventOrganizationDTO> events;
}

