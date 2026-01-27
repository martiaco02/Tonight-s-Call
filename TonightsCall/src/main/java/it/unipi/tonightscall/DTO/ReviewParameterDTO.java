package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a summary of an event's review.
 * <p>
 * Contains the id of the reviewed event, review's text and score.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewParameterDTO {

    /**
     * Unique identifier of the reviewed event.
     */
    @JsonProperty("event_id")
    @Schema(description = "The id of the event reviewed by the user", example = "65c12d3c4d5e6f7g8h9i0j1k")
    private String eventId;

    /**
     * Text of the review.
     */
    @JsonProperty("text")
    @Schema(description = "The text of the review written by the user")
    private String text;

    /**
     * Given score left by the review.
     */
    @JsonProperty("score")
    @Schema(description = "The numerical score (1 - 5) of the event", example = "3")
    private int score;
}
