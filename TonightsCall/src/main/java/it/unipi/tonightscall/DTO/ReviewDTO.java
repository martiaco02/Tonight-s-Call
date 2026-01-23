package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a review left by a user for an event.
 * <p>
 * This object captures the user's feedback, including a numeric score and a text comment.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing a user review for an event")
public class ReviewDTO {

    /**
     * The numeric score assigned to the event.
     * Typically on a scale (from 1 to 5).
     */
    @JsonProperty("score")
    @Schema(description = "Rating score assigned by the user (from 1 to 5)", example = "5")
    private int score;

    /**
     * The username of the user who wrote the review.
     */
    @JsonProperty("username")
    @Schema(description = "Username of the author of the review", example = "Frank")
    private String username;

    /**
     * The textual comment or body of the review.
     */
    @JsonProperty("text")
    @Schema(description = "Textual content of the review", example = "Great atmosphere and amazing music! Highly recommended.")
    private String text;
}
