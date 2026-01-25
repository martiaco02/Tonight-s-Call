package it.unipi.tonightscall.service;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    public final EventRepository eventRepository;
    public final EventGraphRepository eventGraphRepository;

    public final int PAGE_SIZE = 10;

    public EventService(EventRepository eventRepository, EventGraphRepository eventGraphRepository) {
        this.eventRepository = eventRepository;
        this.eventGraphRepository = eventGraphRepository;
    }

    /**
     * Find every event. This method had to be redefined to manage pagination
     *
     * @param pageable used to manage pagination
     */
    public Page<@NonNull Event> getAllEvents(Pageable pageable) { return this.eventRepository.findAll(pageable); }

    /**
     * Find an event given its id
     *
     * @param id event's id
     */
    public Optional<Event> getEventById(String id) { return this.eventRepository.findById(id); }

    /**
     * Find events that contain at least one of the specified topics
     *
     * @param topics the list of topics
     * @param pageable used to manage pagination
     */
    public Page<@NonNull Event> getEventsByTopic(List<String> topics, Pageable pageable) { return this.eventRepository.findByCategoriesIn(topics, pageable); }

    /**
     * Find events that start at the specified date
     *
     * @param startingDate the starting date of the event
     * @param pageable used to manage pagination
     */
    public Page<@NonNull Event> getEventsByDate(LocalDate startingDate, Pageable pageable) { return this.eventRepository.findByStartingDateGreaterThanEqual(startingDate, pageable); }

    /**
     * Find events based on their location
     *
     * @param location the point where events have to be found
     * @param distance the max possible distance from location
     * @param pageable used to manage pagination
     */
    public Page<@NonNull Event> getEventsByLocation(Point location, Distance distance, Pageable pageable) { return this.eventRepository.findByPositionLocationNear(location, distance, pageable); }
}