package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing a review left by a user for an event.
 * <p>
 * This object captures the user's feedback, including a numeric score and a text comment.
 * </p>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Review {

    /**
     * The numeric score assigned to the event.
     * Typically on a scale (from 1 to 5).
     */
    @Field("score")
    private int score;

    /**
     * The username of the user who wrote the review.
     */
    @Field("username")
    private String username;

    /**
     * The textual comment or body of the review.
     */
    @Field("text")
    private String text;
}
