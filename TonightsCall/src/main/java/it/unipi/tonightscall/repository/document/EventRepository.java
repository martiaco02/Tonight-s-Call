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
         * Computes comprehensive statistics for a specific event by aggregating attendee data.
         * <p>
         * The aggregation pipeline performs the following operations:
         * <ol>
         *      <li>Demographics: Calculates the average age of attendees by computing the {@code $dateDiff} between their birth date and the current time.</li>
         *      <li>Financials: Estimates total income by reducing the attendee list and dynamically mapping {@code ticket_type} to its corresponding price in the {@code ticket_price} map.</li>
         *      <li>Geographics: Generates a distribution map of attendee origins (home towns) using a combination of {@code $setUnion}, {@code $map}, and {@code $arrayToObject}.</li>
         *      <li>Engagement: Counts total attendees and retrieves the current average event rating.</li>
         * </ol>
         * </p>
         *
         * @param eventId The unique identifier of the event to analyze.
         * @return A {@link Statistics} object containing demographic, financial, and geographic insights.
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

}