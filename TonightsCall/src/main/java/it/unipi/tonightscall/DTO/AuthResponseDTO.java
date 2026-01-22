package it.unipi.tonightscall.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for the authentication response.
 * <p>
 * This object is returned to the client upon successful login and contains
 * the authentication token (e.g., JWT) required for accessing secured endpoints.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing the response containing the authentication token")
public class AuthResponseDTO {

    /**
     * The authentication token.
     */
    @Schema(
            description = "The JWT (JSON Web Token) generated for the authenticated user",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik..."
    )
    private String token;
}
