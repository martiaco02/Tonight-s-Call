    package it.unipi.tonightscall.service;

    import it.unipi.tonightscall.DTO.EventDTO;
    import it.unipi.tonightscall.DTO.StatisticsDTO;
    import it.unipi.tonightscall.entity.document.*;
    import it.unipi.tonightscall.entity.document.Event;
    import it.unipi.tonightscall.repository.document.UserRepository;
    import it.unipi.tonightscall.utilies.Mapper;
    import it.unipi.tonightscall.repository.document.EventRepository;
    import it.unipi.tonightscall.repository.document.OrganizerRepository;
    import it.unipi.tonightscall.repository.graph.EventGraphRepository;
    import lombok.NonNull;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.geo.Distance;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.data.geo.Point;

    import java.awt.*;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Optional;

    @Service
    public class EventService {

        public final EventRepository eventRepository;
        public final EventGraphRepository eventGraphRepository;

        public final OrganizerRepository organizerRepository;

        public final int PAGE_SIZE = 10;
        private final UserRepository userRepository;

        public EventService(EventRepository eventRepository, EventGraphRepository eventGraphRepository, OrganizerRepository organizerRepository, UserRepository userRepository) {
            this.eventRepository = eventRepository;
            this.eventGraphRepository = eventGraphRepository;
            this.organizerRepository = organizerRepository;
            this.userRepository = userRepository;
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
         * Find all events in a specific city
         *
         * @param cityName The name of the specific city in which the events must be searched
         * @param pageable used to manage pagionation
         */
        public Page<@NonNull EventDTO> getEventsByCity(String cityName,  Pageable pageable) {
            Page<@NonNull Event> events = this.eventRepository.findByPosition_CityName(cityName, pageable);
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
         * @param username The username of the user who requested the update.
         * @param newEventDTO The EventDTO with the updated data.
         * @return The updated EventDTO.
         * @throws RuntimeException If the event is not found, if the user isn't the organizer of the event or if illegal fields are updated
         * @throws IllegalArgumentException If the event doesn't exist
         * @throws IllegalAccessException If the user hasn't got the authorization for modify this event
         */
        @Transactional
        public EventDTO updateEvent(String event_id, String username, EventDTO newEventDTO) throws IllegalAccessException {
            Organizer organizer = organizerRepository.findByUsername(username) //TODO: modificare in modo che anche le organizazzioni abbiano accesso
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

            return Mapper.mapEventToDTO(oldEvent);
        }

        /**
         * This method calculates statistics of a specific event and stores them in it
         *
         * @param event_id      The id of the event
         * @param id            The id of the organizer of the event
         * @return              The StatisticsDTO containing the results
         * @throws IllegalArgumentException If the event or organizer are not found, or if the event was not created by the specific organizer
         */
        @Transactional
        public StatisticsDTO calculateStatistics(String event_id, String id){
            Event event = eventRepository.findById(event_id)
                    .orElseThrow(() -> new IllegalArgumentException("Event Not Found!"));

            AbstracOrganizer abstracOrganizer = organizerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Organizer Not Found!"));

            boolean present = false;
            for(int i=0; i<abstracOrganizer.getEvents().size(); i++){
                if(abstracOrganizer.getEvents().get(i).getId().equals(event_id)){
                    present = true;
                    break;
                }
            }

            if(!present){
                throw new IllegalArgumentException("You can only calculate statistics of your events!");
            }

            if (event.getAttendees() == null || event.getAttendees().isEmpty()) {
                throw new IllegalArgumentException("None has attended the event yet!");
            }

            //Aggregation
            Statistics statistics = eventRepository.calculateStatistics(event_id);
            if(statistics == null){
                throw new IllegalArgumentException("Couldn't calculate statistics!");
            }

            event.setStatistics(statistics);
            eventRepository.save(event);
            return Mapper.mapStatisticsToDto(statistics);
        }

        /**
         * This method marks the event's statistics as "publishable" by setting the specific flag = true.
         * @param event_id The id of the event of which the statistics have to be published.
         * @param id The id of the organizer of the event.
         * @return The EventDTO, containing the statistics.
         * @throws IllegalArgumentException If the event or organizer are not found, or if the event was not created by the specific organizer.
         */
        public EventDTO publishStatistics(String event_id, String id) {
            Event event = eventRepository.findById(event_id)
                    .orElseThrow(() -> new IllegalArgumentException("Event Not Found!"));

            AbstracOrganizer abstracOrganizer = organizerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Organizer Not Found!"));

            boolean present = false;
            for(int i=0; i<abstracOrganizer.getEvents().size(); i++){
                if(abstracOrganizer.getEvents().get(i).getId().equals(event_id)){
                    present = true;
                    break;
                }
            }

            if(!present){
                throw new IllegalArgumentException("You can only calculate statistics of your events!");
            }

            if(event.getStatistics()== null){
                throw new IllegalArgumentException("You need to calculate the statistics of the event!");
            }

            event.getStatistics().setPublish(true);
            eventRepository.save(event);
            return Mapper.mapEventToDTO(event);
        }

        /**
         * This method calculates the number of event per month over a given year, considering a specific city
         * @param city The city where the events must be searched in.
         * @param year The year when the events are held.
         * @param user_id The id of the user who requested the data.
         * @return The list of months and related count of events for each one of them.
         *
         * @throws IllegalArgumentException If user not found or no events found in the specified city
         */
        public List<HashMap<String, Object>> eventDemographic(String city, int year, String user_id){
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new IllegalArgumentException("User Not Found!"));

            if (city == null || city.isBlank())
                throw new IllegalArgumentException("City can't be empty");

            return eventRepository.eventDemographic(city, year);
        }
    }