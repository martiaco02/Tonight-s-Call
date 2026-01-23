package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a user attending an event.
 * <p>
 * This class contains details about the attendee, including personal info
 * and the specific ticket type associated with their participation.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing an attendee of an event")
public class AttendentDTO {

    /**
     * Unique identifier of the attendee.
     */
    @JsonProperty("id")
    @Schema(description = "Unique identifier of the attendee")
    private String id;

    /**
     * The category or type of ticket purchased (e.g., Standard, VIP).
     */
    @JsonProperty("ticket_type")
    @Schema(description = "Type of ticket purchased by the user", example = "VIP")
    private String ticketType;

    /**
     * Contact email of the attendee.
     */
    @JsonProperty("email")
    @Schema(description = "Email address of the attendee", example = "jane.doe@example.com")
    private String email;

    /**
     * Username of the attendee.
     */
    @JsonProperty("username")
    @Schema(description = "Username of the attendee", example = "JaneDoe99")
    private String username;

    /**
     * Name of the attendee's hometown.
     */
    @JsonProperty("home_town")
    @Schema(description = "Name of the attendee's hometown", example = "Milan")
    private String homeTown;

    /**
     * Date of birth of the attendee.
     */
    @JsonProperty("date_of_birth")
    @Schema(description = "Date of birth of the attendee (ISO 8601)", type = "string", format = "date", example = "1995-05-20")
    private LocalDate dateOfBirth;
}
