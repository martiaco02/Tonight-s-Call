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

    /*  C'è la possibilità che findByPositionLocationNear non funzioni perché vuole come parametro un oggetto Location,
        ma a quel punto $near non funziona più perché mongo vuole un Point, quindi o si usa la @Query sotto o si mette
        Point nella classe Location invece di type e coordinates
     */
    /*@Query("""
    {
      'address.loc': {
        $nearSphere: {
          $geometry: { type: 'Point', coordinates: ?0 },
          $maxDistance: ?1
        }
      }
    }
    """)
    Page<Event> findEventsNear(List<Double> coordinates, double maxDistance, Pageable pageable);*/

    /**
     * Find every event containing at least one of the specified topics and starting at the specified date or later
     *
     * @param categories the list of topics
     * @param date the minimum starting date of the events
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByCategoriesInAndStartingDateGreaterThanEqual(List<String> categories, LocalDate date, Pageable pageable);


    @Aggregation(pipeline = {
            // STAGE 0: filtering: taking the event of which we are going to calculate the statistics (?0 = placeholder for first argument from Java method)
            //                     we are considering the event with a specific event_id
            "{'$match':  {'_id':  ?0}}",
            // STAGE 1: Unwinding attendees of the event that is being considered
            "{ '$unwind': { 'path': '$attendees', 'preserveNullAndEmptyArrays': false } }",

            // STAGE 2: Projection: calculating age of attendees and avg
            """
            {
              "$project": {
                "event_score": 1,
                "origin": "$attendees.home_town",
                "age": {
                  "$dateDiff": {
                    "startDate": { "$toDate": "$attendees.date_of_birth" },
                    "endDate": "$$NOW",
                    "unit": "year"
                  }
                },
                "pricePaid": {
                  "$avg": {
                    "$map": {
                      "input": { "$objectToArray": "$ticket_price" },
                      "as": "priceItem",
                      "in": "$$priceItem.v"
                    }
                  }
                }
              }
            }
            """,

            // STAGE 3: Grouping by Event and City
            """
            {
              "$group": {
                "_id": { "id": "$_id", "origin": "$origin" },
                "countPerCity": { "$sum": 1 },
                "partialAgeSum": { "$sum": "$age" },
                "partialIncomeSum": { "$sum": "$pricePaid" },
                "eventScore": { "$first": "$event_score" }
              }
            }
            """,

            // STAGE 4: Grouping by Event
            """
            {
              "$group": {
                "_id": "$_id.id",
                "totalAttenders": { "$sum": "$countPerCity" },
                "totalAgeSum": { "$sum": "$partialAgeSum" },
                "totalIncome": { "$sum": "$partialIncomeSum" },
                "averageRating": { "$first": "$eventScore" },
                "originList": {
                  "$push": { "k": "$_id.origin", "v": "$countPerCity" }
                }
              }
            }
            """,

            // STAGE 5: Putting right fields for Statistics object
            """
            {
              "$project": {
                  "date_update": "$$NOW",
                  "total_attenders": "$totalAttenders",
                  "publish": {"$literal": false},
                  "predicted_income": "$totalIncome",
                  "average_rating": { "$ifNull": ["$averageRating", 0] },
                  "average_age": {
                    "$cond": [
                      { "$eq": ["$totalAttenders", 0] },
                      0,
                      { "$divide": ["$totalAgeSum", "$totalAttenders"] }
                    ]
                  },
                  "origin_attenders": { "$arrayToObject": "$originList" }
              }
            }
            """
    })
    Statistics calculateStatistics(String eventId);

}