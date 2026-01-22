package it.unipi.tonightscall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded Entity representing a summary of an event review.
 * <p>
 * This object is stored directly inside the User document list 'reviewed_events'.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    /**
     * The name of the reviewed event.
     * <p>
     * Mapped to the field "event_name" in MongoDB
     * </p>
     */
    @Field("event_name")
    private String eventName;

    /**
     * The score given to the event.
     */
    @Field("score")
    private Integer score;
}
