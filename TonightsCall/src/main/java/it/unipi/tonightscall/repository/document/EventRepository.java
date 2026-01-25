package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.entity.document.Organization;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.awt.*;
import java.time.LocalDate;
import java.util.Collection;
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
    Page<@NonNull Event> findByCategoriesIn(Collection<List<String>> categories, Pageable pageable);

    /**
     * Find events that contain every specified topic
     *
     * @param categories the list of topics
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByCategoriesAll(Collection<List<String>> categories, Pageable pageable);

    /**
     * Find events that contain at least one of the specified topics
     *
     * @param startingDate the starting date of the event
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByStartingDateGreaterThanEqual(LocalDate startingDate, Pageable pageable);

    /**
     * Find events based on their location
     *
     * @param location the point where events have to be found
     * @param distance the max possible distance from location
     * @param pageable used to manage pagination
     */
    Page<@NonNull Event> findByAddressLocationNear(Point location, Distance distance, Pageable pageable);
}
