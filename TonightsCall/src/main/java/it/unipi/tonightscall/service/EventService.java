package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.UserDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.entity.graph.EventNode;
import it.unipi.tonightscall.entity.graph.TopicNode;
import it.unipi.tonightscall.entity.graph.UserNode;
import it.unipi.tonightscall.repository.document.AbstractOrganizerRepository;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.repository.graph.TopicGraphRepository;
import it.unipi.tonightscall.repository.graph.UserGraphRepository;
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
import org.springframework.data.geo.Point;

import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventGraphRepository eventGraphRepository;
    private final UserRepository userRepository;
    private final UserGraphRepository userGraphRepository;
    private final TopicGraphRepository topicGraphRepository;

    private final OrganizerRepository organizerRepository;

    public final int PAGE_SIZE = 10;
    private final AbstractOrganizerRepository abstractOrganizerRepository;

    public EventService(EventRepository eventRepository, EventGraphRepository eventGraphRepository, UserRepository userRepository, UserGraphRepository userGraphRepository, TopicGraphRepository topicGraphRepository, OrganizerRepository organizerRepository, AbstractOrganizerRepository abstractOrganizerRepository) {
        this.eventRepository = eventRepository;
        this.eventGraphRepository = eventGraphRepository;
        this.userRepository = userRepository;
        this.userGraphRepository = userGraphRepository;
        this.topicGraphRepository = topicGraphRepository;
        this.organizerRepository = organizerRepository;
        this.abstractOrganizerRepository = abstractOrganizerRepository;
    }

    /**
     * Find every event. This method had to be redefined to manage pagination
     *
     * @param pageable used to manage pagination
     */
    public Page<@NonNull EventDTO> getAllEvents(Pageable pageable) {
        return this.eventRepository.findAll(pageable).map(Mapper::mapEventToDTO);
    }

    /**
     * Find an event given its id
     *
     * @param id event's id
     * @return the event having the id's params or null if it doesn't exist
     */
    public EventDTO getEventById(String id) {
        Optional<Event> event = this.eventRepository.findById(id);
        return event.map(Mapper::mapEventToDTO).orElse(null);
    }

    /**
     * Find events that contain at least one of the specified topics
     *
     * @param topics the list of topics
     * @param pageable used to manage
     *
     * @return Page<EventDTO> conteing the events or a null value if nothing was found
     */
    public Page<@NonNull EventDTO> getEventsByTopic(List<String> topics, Pageable pageable) {
        Page<@NonNull Event> events = this.eventRepository.findByCategoriesIn(topics, pageable);
        if (events.isEmpty())
            return null;

        return events.map(Mapper::mapEventToDTO);
    }

    /**
     * Find events that start at the specified date or later
     *
     * @param startingDate the starting date of the event
     * @param pageable used to manage pagination
     *
     * @return Page<EventDTO> conteing the events or a null value if nothing was found
     */
    public Page<@NonNull EventDTO> getEventsByDate(LocalDate startingDate, Pageable pageable) {
        Page<@NonNull Event> events = this.eventRepository.findByStartingDateGreaterThanEqual(startingDate, pageable);
        if (events.isEmpty())
            return null;

        return events.map(Mapper::mapEventToDTO);
    }

    /**
     * Find events based on their location
     *
     * @param location the point where events have to be found
     * @param distance the max possible distance from location
     * @param pageable used to manage pagination
     */
    public Page<@NonNull EventDTO> getEventsByLocation(Point location, Distance distance, Pageable pageable) {
        Page<@NonNull Event> events = this.eventRepository.findByPositionLocationNear(location, distance, pageable);
        if (events.isEmpty())
            return null;
        return events.map(Mapper::mapEventToDTO);
    }

    /**
     * Find every event containing at least one of the specified topics and starting at the specified date or later
     *
     * @param categories the list of topics
     * @param date the minimum starting date of the events
     * @param pageable used to manage pagination
     */
    public Page<@NonNull EventDTO> getEventsByTopicAndDate(List<String> categories, LocalDate date, Pageable pageable) {

        Page<@NonNull Event> events = this.eventRepository.findByCategoriesInAndStartingDateGreaterThanEqual(categories, date, pageable);
        if (events.isEmpty())
            return null;

        return events.map(Mapper::mapEventToDTO);
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
     * @param id The id of the user who requested the update.
     * @param newEventDTO The EventDTO with the updated data.
     * @return The updated EventDTO.
     * @throws RuntimeException If the event is not found, if the user isn't the organizer of the event or if illegal fields are updated
     * @throws IllegalArgumentException If the event doesn't exist
     * @throws IllegalAccessException If the user hasn't got the authorization for modify this event
     */
    @Transactional
    public List<String> updateEvent(String event_id, String id, EventDTO newEventDTO) throws IllegalAccessException {
        AbstracOrganizer organizer = abstractOrganizerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizer Not Found!"));

        boolean flag = false;
        for (EventOrganization eo : organizer.getEvents()) {
            if (eo.getId().equals(event_id)) {
                flag = true;
                break;
            }
        }
        if (!flag)
            throw new IllegalAccessException("This Organizer or Organization can't modify this event");

        //  checking if the event exists
        Event oldEvent = eventRepository.findById(event_id)
                .orElseThrow(() -> new IllegalArgumentException("Event Not Found!"));

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

        // updating the new data

        boolean flagModifyGraph = false;

        //  Location
        if(newEventDTO.getPosition() != null){
            oldEvent.setPosition(Mapper.mapAddressToEntity(newEventDTO.getPosition()));
            flagModifyGraph = true;
        }

        //  Starting date
        if(newEventDTO.getStartingDate() != null){
            oldEvent.setStartingDate(newEventDTO.getStartingDate());
            flagModifyGraph = true;
        }

        //  Ending date
        if(newEventDTO.getEndingDate() != null){
            if (oldEvent.getStartingDate().isAfter(oldEvent.getEndingDate()))
                throw new RuntimeException("The new ending date is before the starting date!");

            oldEvent.setEndingDate(newEventDTO.getEndingDate());
            flagModifyGraph = true;
        }

        //  Topics
        if(newEventDTO.getCategories() != null){
            oldEvent.setCategories(newEventDTO.getCategories());
            flagModifyGraph = true;
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

        if (flagModifyGraph){
            EventNode eventNode = eventGraphRepository.findById(oldEvent.getId())
                    .orElseThrow(() -> new RuntimeException("Event Node Not Found!"));

            eventNode.setStartingDate(oldEvent.getStartingDate());
            eventNode.setEndingDate(oldEvent.getEndingDate());
            eventNode.setCoordinates(oldEvent.getPosition().getLocation().getCoordinates());

            eventGraphRepository.deleteAllIsAboutRelationships(oldEvent.getId());
            eventNode.setCategories(new HashSet<>());
            for (String category : oldEvent.getCategories()) {
                TopicNode categoryNode = topicGraphRepository.findById(category)
                        .orElse(new TopicNode(category));

                eventNode.getCategories().add(categoryNode);
                topicGraphRepository.save(categoryNode);
            }

            eventGraphRepository.save(eventNode);

        }

        return eventGraphRepository.findAllEmailFromAttendingUser(oldEvent.getId());
    }
}