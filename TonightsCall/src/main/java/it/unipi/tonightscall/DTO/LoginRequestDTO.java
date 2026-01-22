package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used for user authentication.
 * <p>
 * This object carries the credentials (username and password) sent by the client
 * to the server to perform a login operation.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing the credentials required for user login")
public class LoginRequestDTO {

    /**
     * The username of the user attempting to log in.
     */
    @JsonProperty("username")
    @Schema(
            description = "The unique username of the user",
            example = "Frank",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    /**
     * The password of the user.
     */
    @JsonProperty("password")
    @Schema(
            description = "The password associated with the account",
            example = "SecretPass123!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
