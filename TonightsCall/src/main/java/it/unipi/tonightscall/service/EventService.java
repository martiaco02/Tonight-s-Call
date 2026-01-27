package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.entity.document.EventOrganization;
import it.unipi.tonightscall.entity.document.Organizer;
import it.unipi.tonightscall.utilies.Mapper;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    public final EventRepository eventRepository;
    public final EventGraphRepository eventGraphRepository;

    public final OrganizerRepository organizerRepository;

    public final int PAGE_SIZE = 10;

    public EventService(EventRepository eventRepository, EventGraphRepository eventGraphRepository, OrganizerRepository organizerRepository) {
        this.eventRepository = eventRepository;
        this.eventGraphRepository = eventGraphRepository;
        this.organizerRepository = organizerRepository;
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
     * Find events that start at the specified date or later
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

    /**
     * Find every event containing at least one of the specified topics and starting at the specified date or later
     *
     * @param categories the list of topics
     * @param date the minimum starting date of the events
     * @param pageable used to manage pagination
     */
    public Page<@NonNull Event> getEventsByTopicAndDate(List<String> categories, LocalDate date, Pageable pageable) {
        return this.eventRepository.findByCategoriesInAndStartingDateGreaterThanEqual(categories, date, pageable);
    }

    /**
     * Updates an Event's data
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the Event exists.</li>
     *      <li>Checks if the Organizer who requested the Event update is the owner of the Event</li>
     *      <li>Checks for consistency: ??? can't be changed</li>
     *      <li>Updates the specific data for the Event</li>
     *      <li>Saves the updated Event to MongoDB.</li>
     *      <li>Updates the corresponding node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param event_id The id of the Event that will be updated.
     * @param username The username of the user who requested the update.
     * @param newEventDTO The EventDTO with the updated data.
     * @return The updated EventDTO.
     * @throws RuntimeException If the event is not found, if the user isn't the organizer of the event or if illegal fields are updated
     */

    @Transactional
    public EventDTO updateEvent(String event_id, String username, EventDTO newEventDTO){
        Organizer organizer = organizerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));

        //  checking if the event exists
        Event oldEvent = eventRepository.findById(event_id)
                .orElseThrow(() -> new RuntimeException("Event Not Found!"));

        // checking consistency: event id can't change
        if(newEventDTO.getId() != null && !oldEvent.getId().equals(newEventDTO.getId())){
            throw new RuntimeException("Can't change event ID!");
        }

        // checking consistency: event name can't change
        if(newEventDTO.getEventName() != null && !oldEvent.getEventName().equals(newEventDTO.getEventName())){
            throw new RuntimeException("Can't change event name!");
        }

        // checking consistency: event reviews can't change
        if(newEventDTO.getReviews() != null) {
            throw new RuntimeException("Can't change event reviews!");
        }

        // checking consistency: event attendees can't change
        if(newEventDTO.getAttendees() != null){
            throw new RuntimeException("Can't change event attendees!");
        }

        // checking consistency: event statistics can't change
        if(newEventDTO.getStatistic() != null){
            throw new RuntimeException("Can't change the statistics of the event!");
        }

        // checking if the user is the organizer of the event
        List<EventOrganization> organizer_events = organizer.getEvents();

        for(EventOrganization e : organizer_events) {
            if(e.getId().equals(event_id)) { break; }
            throw new RuntimeException("The user is not the organizer of this event!");
        }

        // updating the new data

        //  Location
        if(newEventDTO.getPosition() != null){
            oldEvent.setPosition(Mapper.mapAddressToEntity(newEventDTO.getPosition()));
        }

        //  Ticket price
        if(newEventDTO.getTicketPrice() != null){
            oldEvent.setTicketPrice(newEventDTO.getTicketPrice());
        }

        //  Starting date
        if(newEventDTO.getStartingDate() != null){
            oldEvent.setStartingDate(newEventDTO.getStartingDate());
        }

        //  Ending date
        if(newEventDTO.getEndingDate() != null){
            oldEvent.setEndingDate(newEventDTO.getEndingDate());
        }

        //  Topics
        if(newEventDTO.getCategories() != null){
            oldEvent.setCategories(newEventDTO.getCategories());
        }

        //  Description
        if(newEventDTO.getDescription() != null){
            oldEvent.setDescription(newEventDTO.getDescription());
        }

        //  Poster
        if(newEventDTO.getUrlImg() != null){
            oldEvent.setUrlImg(newEventDTO.getUrlImg());
        }

        //  Starting times
        if(newEventDTO.getStartingTimes() != null){
            oldEvent.setStartingTimes(newEventDTO.getStartingTimes());
        }

        // updating document
        eventRepository.save(oldEvent);

        return Mapper.mapEventToDto(oldEvent);
    }
}