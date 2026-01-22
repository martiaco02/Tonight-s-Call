package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizationDTO extends AbstracOrganizerDTO {

    @JsonProperty("list_of_members")
    private List<MembersDTO> members;

    @JsonProperty("pending_requests")
    private List<RequestDTO> pendingRequests;

}
