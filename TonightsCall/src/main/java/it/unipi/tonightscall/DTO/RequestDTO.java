package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a request to join an organization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing a user request")
public class RequestDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the organizer that whant to join the organization", example = "65a12b3c4d5e6f7g8h9i0j1k")
    private String id;

    @JsonProperty("username")
    @Schema(description = "Username of the requester", example = "wannabe_member")
    private String username;
}
