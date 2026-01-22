package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a summary of a review left by the user.
 * <p>
 * Contains the name of the event reviewed and the score assigned.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing a review summary for a specific event")
public class ReviewEventDTO {

    /**
     * The name of the event being reviewed.
     */
    @JsonProperty("event_name")
    @Schema(description = "The name of the event reviewed by the user", example = "Summer Music Festival 2024")
    private String eventName;

    /**
     * The rating score given to the event.
     */
    @JsonProperty("score")
    @Schema(description = "The rating score assigned to the event from 1 to 5)", example = "5")
    private Integer score;
}
