package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a User in the system.
 * <p>
 * This class conveys personal information, social connections, and references
 * to related entities such as the user's hometown and their event reviews.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing the details of a User")
public class UserDTO {

    /**
     * The unique identifier of the user.
     * Mapped to "_id" in the JSON structure.
     */
    @JsonProperty("_id")
    @Schema(description = "Unique identifier of the User", example = "65a12b3c4d5e6f7g8h9i0j1k")
    private String id;

    /**
     * The first name of the user.
     */
    @JsonProperty("name")
    @Schema(description = "First name of the User", example = "Francesco")
    private String name;

    /**
     * The last name (surname) of the user.
     */
    @JsonProperty("lastname")
    @Schema(description = "Lastname of the User", example = "Della Maggiora")
    private String lastname;

    /**
     * The email address of the user.
     */
    @JsonProperty("email")
    @Schema(description = "Email of the User", example = "f.dellamaggiora2@studenti.unipi.it")
    private String email;

    /**
     * The unique username chosen by the user.
     */
    @JsonProperty("username")
    @Schema(description = "Username of the User", example = "Frank")
    private String username;


    /**
     * The password of the user.
     * <p>
     * <b>SECURITY NOTE:</b> Marked as WRITE_ONLY. It allows reading the password from
     * the request JSON (for registration/login), but explicitly hides it in the
     * response JSON to prevent leaking sensitive credentials.
     * </p>
     */
    @JsonProperty(value="password", access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "The user's password. This field is write-only and will not be returned in responses.",
            accessMode = Schema.AccessMode.WRITE_ONLY,
            example = "SecretPass123!"
    )
    private String password;

    /**
     * The user's date of birth.
     * Mapped to "date_of_birth" in the JSON structure.
     */
    @JsonProperty("date_of_birth")
    @Schema(
            description = "The user's date of birth (ISO 8601 format)",
            type = "string",
            format = "date",
            example = "2002-02-18")
    private LocalDate dateOfBirth;

    /**
     * A list of topics or activities the user is interested in.
     */
    @Schema(description = "List of the User's interests", example = "[\"TECH\", \"MUSIC\"]")
    private List<String> interests;

    /**
     * A list of usernames representing the user's friends.
     */
    @Schema(description = "List of the user's friends", example = "[\"Marty\", \"AXB\"]")
    private List<String> friends;

    /**
     * Detailed information about the user's hometown.
     * Mapped to "home_town" in the JSON structure.
     */
    @JsonProperty("home_town")
    @Schema(description = "Nested document with the information of the User's home town")
    private AddressDTO homeTown;

    /**
     * A list of events reviewed by this user.
     * Mapped to "reviewed_events" in the JSON structure.
     */
    @JsonProperty("reviewed_events")
    @Schema(description = "Nested document with review written by the user")
    private List<ReviewEventDTO> reviewedEvents;
}