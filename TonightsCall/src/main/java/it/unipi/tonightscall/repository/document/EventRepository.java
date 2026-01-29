package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.entity.document.Statistics;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

    /**
     * MongoDB Repository for managing {@link Event} entities.
     * <p>
     * This interface handles operations for events.
     * Each time a find is performed, the result is paginated to limit the number of results
     * </p>
     */

public interface EventRepository extends MongoRepository<@NonNull Event, @NonNull String> {
    /**
     * Find every event. This method had to be redefined to manage pagination
     *
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findAll(Pageable pageable);

    /**
     * Find events that contain at least one of the specified topics
     *
     * @param categories the list of topics
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByCategoriesIn(List<String> categories, Pageable pageable);

    /**
     * Find events that start at the specified date or later
     *
     * @param startingDate the minimum starting date of the events
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByStartingDateGreaterThanEqual(LocalDate startingDate, Pageable pageable);

        /**
         * Find events of a specific city
         * @param cityName the specified city in which to search for available events
         * @param pageable used to manage pagination
         */
    Page<@NonNull Event> findByPosition_CityName(String cityName, Pageable pageable);

    /**
     * Given the coordinates of a point [lon, lat], find every event near that point, given a maximum admissible distance
     *
     * @param location the point where events have to be found
     * @param distance the max admissible distance from location
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByPositionLocationNear(Point location, Distance distance, Pageable pageable);

    /**
     * Find every event containing at least one of the specified topics and starting at the specified date or later
     *
     * @param categories the list of topics
     * @param date the minimum starting date of the events
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByCategoriesInAndStartingDateGreaterThanEqual(List<String> categories, LocalDate date, Pageable pageable);


    /**
     * Aggregates and computes detailed analytics for a specific event.
     * <p>
     * The pipeline performs a multi-stage transformation:
     * <ul>
     *  <li>Filters by event ID.</li>
     *  <li> Pre-calculates core metrics:
     *      <ul>
     *          <li>Calculates attendee ages using {@code $dateDiff} between their birth date and the current time.</li>
     *          <li>Computes dynamic income by matching attendee {@code ticket_type} with the event's {@code ticket_price} map.</li>
     *          <li>Builds a frequency list of attendee origins (home towns).</li>
     *      </ul>
     * </li>
     * <li> Finalizes metrics, including average age and converts the origin list into a structured map (object).</li>
     * </ul>
     * </p>
     *
     * @param eventId The unique identifier of the event.
     * @return A {@link Statistics} object containing demographic, financial, and geographic data.
     */
    @Aggregation(pipeline = {
        "{ '$match': {'_id': ?0} }",
        """
        {
            "$project": {
                "total_attenders": { "$size": { "$ifNull": ["$attendees", []] } },
                "average_rating": { "$ifNull": ["$event_score", 0] },
                "date_update": "$$NOW",
                "publish": { "$literal": false },
                "totalAgeSum": {
                    "$reduce": {
                        "input": { "$ifNull": ["$attendees", []] },
                        "initialValue": 0,
                        "in": {
                            "$add": [
                                "$$value",
                                {
                                    "$dateDiff": {
                                        "startDate": { "$toDate": "$$this.date_of_birth" },
                                        "endDate": "$$NOW",
                                        "unit": "year"
                                    }
                                }
                            ]
                        }
                    }
                },
                "calculatedIncome": {
                    "$reduce": {
                        "input": { "$ifNull": ["$attendees", []] },
                        "initialValue": 0,
                        "in": {
                            "$add": [
                                "$$value",
                                { "$ifNull": [ { "$getField": { "field": "$$this.ticket_type", "input": "$ticket_price" } }, 0 ] }
                            ]
                        }
                    }
                },
                "originList": {
                    "$map": {
                        "input": { "$setUnion": { "$ifNull": ["$attendees.home_town", []] } },
                        "as": "city",
                        "in": {
                            "k": "$$city",
                            "v": {
                                "$size": {
                                    "$filter": {
                                        "input": { "$ifNull": ["$attendees", []] },
                                        "as": "att",
                                        "cond": { "$eq": ["$$att.home_town", "$$city"] }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        """,
        """
        {
            "$project": {
                "date_update": 1,
                "total_attenders": 1,
                "publish": 1,
                "average_rating": 1,
                "predicted_income": { "$ifNull": ["$calculatedIncome", 0] },
                "average_age": {
                    "$cond": [
                        { "$eq": ["$total_attenders", 0] },
                        0,
                        { "$divide": ["$totalAgeSum", { "$cond": [{ "$eq": ["$total_attenders", 0] }, 1, "$total_attenders"] }] }
                    ]
                },
                "origin_attenders": { "$arrayToObject": "$originList" }
            }
        }
        """
    })
    Statistics calculateStatistics(String eventId);

    /**
    * Generates a monthly distribution of events for a specific city and year.
    * <p>
    *   This query is designed for time-series analysis and ensures a complete data set:
    *   <ul>
    *   <li>Filtering: Matches events based on the specified city name and year of the {@code starting_date}.</li>
    *   <li>Grouping: Aggregates the count of events occurring in each month.</li>
    *   <li>Densification: Uses {@code $densify} to ensure all 12 months (1-12) are present in the output,  filling missing months with zero values.</li>
    *   <li>Projection: Maps month numbers to their full English names  and formats the final output map.</li>
    * </ul>
    * </p>
    *
    * @param city The name of the city to filter by.
    * @param year The year to analyze.
    * @return A list of maps, each containing the city, year, month name, and event count.
    */
    @Aggregation(pipeline = {
        """
        {
            "$match": {
                "position.city_name": ?0, 
                "$expr": { "$eq": [ { "$year": "$starting_date" }, ?1 ] }
            }
        }
        """,
        """
        {
            "$group": {
                "_id": { "$month": "$starting_date" },
                "total_count": { "$sum": 1 }
            }
        }
        """,
        """
        {
            "$densify": {
                "field": "_id",
                "range": {
                    "step": 1,
                    "bounds": [1, 13]
                }
            }
        }
        """,
        "{ '$sort': { '_id': 1 } }",
        """
        {
            "$project": {
                "city": ?0,
                "year": {"$literal": ?1},
                "month": {
                    "$arrayElemAt": [
                        [ "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ],
                        "$_id"
                    ]
                },
                "event_count": { "$ifNull": ["$total_count", 0] },
                "_id": 0
            }
        }
        """
    })
    List<HashMap<String, Object>> eventDemographic(String city, int year);

}