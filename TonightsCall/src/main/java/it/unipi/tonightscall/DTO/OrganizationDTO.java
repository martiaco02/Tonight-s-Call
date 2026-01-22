package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * DTO representing an Organization (e.g., a company or a group).
 * <p>
 * Extends the abstract base class with management of members and pending requests.
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Model representing an Organization entity")
public class OrganizationDTO extends AbstracOrganizerDTO {

    @JsonProperty("list_of_members")
    @Schema(description = "List of members belonging to this organization")
    private List<MembersDTO> members;

    @JsonProperty("pending_requests")
    @Schema(description = "List of pending requests from users wanting to join the organization")
    private List<RequestDTO> pendingRequests;

}
