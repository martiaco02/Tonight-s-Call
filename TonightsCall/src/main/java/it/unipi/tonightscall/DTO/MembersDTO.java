package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a member within an organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of a member belonging to an organization")
public class MembersDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the member", example = "usr_9876")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Name of the member", example = "Mario Rossi")
    private String name;

    @JsonProperty("password")
    @Schema(
            description = "Member's password (Write-only)",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;
}
