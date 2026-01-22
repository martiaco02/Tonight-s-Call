package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing an individual Organizer.
 * <p>
 * Extends the abstract base class with personal details such as username,
 * last name, and date of birth.
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing an individual Organizer")
public class OrganizerDTO extends AbstracOrganizerDTO {

    @JsonProperty("lastname")
    @Schema(description = "Last name of the organizer", example = "Rossi")
    private String lastName;

    @JsonProperty("username")
    @Schema(description = "Unique username", example = "event_master_99")
    private String username;

    @JsonProperty("date_of_birth")
    @Schema(description = "Date of birth (ISO 8601 format)", type = "string", format = "date", example = "1990-01-01")
    private LocalDate dateOfBirth;

    @JsonProperty("password")
    @Schema(
            description = "Password for authentication. Write-only for security.",
            accessMode = Schema.AccessMode.WRITE_ONLY,
            example = "SecretPass123!"
    )
    private String password;

    @JsonProperty("organizations")
    @Schema(description = "List of organizations this individual belongs to")
    private List<OrganizationForLinkingDTO> organizations;
}


