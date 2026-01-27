package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.geo.Point;

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
}