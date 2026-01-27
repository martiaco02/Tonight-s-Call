package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Embedded Entity representing an event.
 * Contains all information about an event, including location,
 * schedule, pricing, and aggregated user feedback (reviews/score).
 */
@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    /**
     * Unique identifier of the event.
     */
    @Id
    private String id;

    /**
     * Name or title of the event.
     */
    @Field("event_name")
    private String eventName;

    /**
     * Location of the event.
     * Uses the HomeTownDTO structure to store city name and coordinates.
     */
    @Field("position")
    private Address position;

    /**
     * Map of ticket types and their costs.
     * Example: {"Standard": 20.0, "VIP": 50.0}
     */
    @Field("ticket_price")
    private Object ticketPrice;

    /**
     * The date when the event starts.
     */
    @Field("starting_date")
    private LocalDate startingDate;

    /**
     * The date when the event ends.
     */
    @Field("ending_date")
    private LocalDate endingDate;

    /**
     * List of categories or genres associated with the event (e.g., "Music", "Rock").
     */
    @Field("categories")
    private List<String> categories;

    /**
     * Detailed textual description of the event.
     */
    @Field("description")
    private String description;

    /**
     * URL of the event's promotional image or poster.
     */
    @Field("url_img")
    private String urlImg;

    /**
     * Map containing schedule details.
     * Could map dates to specific times or stages to lineups.
     */
    @Field("starting_times")
    private Object startingTimes;

    /**
     * List of user reviews for this event.
     */
    @Field("reviews")
    private List<Review> reviews;

    /**
     * Total count of reviews received.
     */
    @Field("total_review")
    private int totalReview;

    /**
     * Average score of the event (from 1.0 to 5.0).
     */
    @Field("event_score")
    private double eventScore;

    /**
     * List of users planning to attend the event.
     */
    @Field("attendees")
    private List<Attendent> attendees;

    /**
     * Detailed statistical data about the event
     */
    @Field("statistic")
    private Statistics statistics;
}
