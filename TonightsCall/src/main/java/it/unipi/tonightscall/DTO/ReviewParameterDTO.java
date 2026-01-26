package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String eventId;

    /**
     * Text of the review.
     */
    @JsonProperty("text")
    private String text;

    /**
     * Given score left by the review.
     */
    @JsonProperty("score")
    private int score;
}
