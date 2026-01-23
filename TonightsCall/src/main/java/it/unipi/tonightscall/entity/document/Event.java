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

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    private String id;

    @Field("event_name")
    private String eventName;

    @Field("position")
    private HomeTown position;

    @Field("ticket_price")
    private Map<String, Double> ticketPrice;

    @Field("starting_date")
    private LocalDate startingDate;

    @Field("ending_date")
    private LocalDate endingDate;

    @Field("categories")
    private List<String> categories;

    @Field("description")
    private String description;

    @Field("url_img")
    private String urlImg;

    @Field("starting_times")
    private Map<String, Object> startingTimes;

    @Field("reviews")
    private List<Review> reviews;

    @Field("total_review")
    private int totalReview;

    @Field("event_score")
    private double eventScore;

    @Field("attendees")
    private List<Attendent> attendees;

    @Field("statistic")
    private Statistics statistics;
}
