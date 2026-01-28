package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing the full details of an Event.
 * <p>
 * This object contains all public information about an event, including location,
 * schedule, pricing, and aggregated user feedback (reviews/score).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing the details of an Event")
public class EventDTO {

    /**
     * Unique identifier of the event.
     */
    @JsonProperty("_id")
    private String id;

    /**
     * Name or title of the event.
     */
    @JsonProperty("event_name")
    @Schema(description = "Name or title of the event", example = "Summer Jazz Festival 2024")
    private String eventName;

    /**
     * Location of the event.
     * Uses the HomeTownDTO structure to store city name and coordinates.
     */
    @JsonProperty("position")
    @Schema(description = "Geographical location of the event")
    private AddressDTO position;

    /**
     * Map of ticket types and their costs.
     * Example: {"Standard": 20.0, "VIP": 50.0}
     */
    @JsonProperty("ticket_price")
    @Schema(description = "Map of ticket categories and their prices", example = "{\"Standard\": 20.0, \"VIP\": 50.0}")
    private Map<String, Double> ticketPrice;

    /**
     * The date when the event starts.
     */
    @JsonProperty("starting_date")
    @Schema(description = "Start date of the event (ISO 8601)", type = "string", format = "date", example = "2024-07-15")
    private LocalDate startingDate;

    /**
     * The date when the event ends.
     */
    @JsonProperty("ending_date")
    @Schema(description = "End date of the event (ISO 8601)", type = "string", format = "date", example = "2024-07-17")
    private LocalDate endingDate;

    /**
     * List of categories or genres associated with the event (e.g., "Music", "Rock").
     */
    @JsonProperty("categories")
    @Schema(description = "List of tags/categories describing the event", example = "[\"Music\", \"Live\", \"Jazz\"]")
    private List<String> categories;

    /**
     * Detailed textual description of the event.
     */
    @JsonProperty("description")
    @Schema(description = "Full description of the event", example = "Three days of live jazz music featuring top international artists.")
    private String description;

    /**
     * URL of the event's promotional image or poster.
     */
    @JsonProperty("url_img")
    @Schema(description = "URL of the promotional image", example = "https://example.com/images/jazz-fest.jpg")
    private String urlImg;

    /**
     * Map containing schedule details.
     * Could map dates to specific times or stages to lineups.
     */
    @JsonProperty("starting_times")
    @Schema(description = "Flexible structure containing schedule or timing details", example = "{\"Day 1\": \"18:00\", \"Day 2\": \"20:00\"}")
    private Object startingTimes;

    /**
     * List of user reviews for this event.
     */
    @JsonProperty("reviews")
    @Schema(description = "List of reviews left by users")
    private List<ReviewDTO> reviews;

    /**
     * Total count of reviews received.
     */
    @JsonProperty("total_review")
    @Schema(description = "Total number of reviews", example = "150")
    private Integer totalReview;

    /**
     * Average score of the event (from 1.0 to 5.0).
     */
    @JsonProperty("event_score")
    @Schema(description = "Average rating score of the event", example = "4.5")
    private Double eventScore;

    /**
     * List of users planning to attend the event.
     */
    @JsonProperty("attendees")
    @Schema(description = "List of users attending the event")
    private List<AttendentDTO> attendees;

    /**
     * Detailed statistical data about the event
     */
    @JsonProperty("statistic")
    @Schema(description = "Detailed statistics related to the event")
    private StatisticsDTO statistic;

}
