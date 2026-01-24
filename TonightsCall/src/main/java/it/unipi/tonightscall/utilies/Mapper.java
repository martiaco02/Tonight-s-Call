package it.unipi.tonightscall.utilies;

import it.unipi.tonightscall.DTO.*;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.graph.EventNode;
import it.unipi.tonightscall.entity.graph.OrganizerNode;
import it.unipi.tonightscall.entity.graph.UserNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for mapping objects between the Data Transfer Object (DTO) layer
 * and the Persistence (Entity) layer.
 * <p>
 * This class provides static methods to convert DTO to User and vice versa.
 * It manually handles nested objects such as HomeTown and ReviewEvents to ensure
 * precise control over the data structure.
 * </p>
 */
public class Mapper {

    /**
     * Converts a User Entity (Database object) into a UserDTO (API object).
     *
     * @param entity The User entity retrieved from MongoDB.
     * @return A UserDTO populated with the entity's data, or null if the input is null.
     */
    public static UserDTO mapUserToDto(User entity) {

        if (entity == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastname(entity.getLastname());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setInterests(entity.getInterests());
        dto.setFriends(entity.getFriends());

        Address addressEntity = entity.getAddress();

        if  (addressEntity != null) {
            Location locationEntity = addressEntity.getLocation();

            if (locationEntity != null) {
                dto.setHomeTown(
                        new AddressDTO(
                                addressEntity.getCityName(),
                                addressEntity.getFullAddress(),
                                new LocationDTO(
                                        locationEntity.getType(),
                                        locationEntity.getCoordinates()
                                )
                        )
                );
            }
           else {
               dto.setHomeTown(
                       new AddressDTO(
                               addressEntity.getCityName(),
                               addressEntity.getFullAddress(),
                               null
                       )
               );
            }
        } else {
            dto.setHomeTown(new AddressDTO());
        }

        List<ReviewEvent> reviewedEvents = entity.getReviewedEvents();
        if (reviewedEvents != null) {
            List<ReviewEventDTO> reviewedEventsDTO = new ArrayList<>();
            for (ReviewEvent event : reviewedEvents) {
                reviewedEventsDTO.add(new  ReviewEventDTO(
                        event.getEventName(),
                        event.getScore()
                ));
            }
            dto.setReviewedEvents(reviewedEventsDTO);

        } else {
            dto.setReviewedEvents(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Converts a UserDTO (API object) into a User Entity (Database object).
     * <p>
     * <b>Note:</b> The password is NOT mapped here directly. It is usually handled
     * by the Service layer which encodes it before saving.
     * </p>
     *
     * @param dto The UserDTO received from the client.
     * @return A User entity ready to be saved/updated in MongoDB, or null if input is null.
     */
    public static User mapUserToEntity(UserDTO dto) {

        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLastname(dto.getLastname());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setInterests(dto.getInterests());
        entity.setFriends(dto.getFriends());

        if (dto.getHomeTown() != null) {
            Address ht = new Address();
            ht.setCityName(dto.getHomeTown().getCityName());

            if (dto.getHomeTown().getLoc() != null) {
                Location loc = new Location();
                loc.setType(dto.getHomeTown().getLoc().getType());
                loc.setCoordinates(dto.getHomeTown().getLoc().getCoordinates());
                ht.setLocation(loc);
            }
            entity.setAddress(ht);
        } else {
            entity.setAddress(new Address());
        }

        if (dto.getReviewedEvents() != null) {
            List<ReviewEvent> events = new ArrayList<>();
            for (ReviewEventDTO revDto : dto.getReviewedEvents()) {
                ReviewEvent rev = new ReviewEvent();
                rev.setEventName(revDto.getEventName());
                rev.setScore(revDto.getScore());
                events.add(rev);
            }
            entity.setReviewedEvents(events);
        } else {
            entity.setReviewedEvents(new ArrayList<>());
        }

        return entity;
    }

    /**
     * Converts an OrganizerDTO (API object) into an Organizer Entity (Database object).
     * <p>
     * Handles mapping of specific fields like events list and linked organizations.
     * </p>
     *
     * @param dto The OrganizerDTO containing individual organizer details.
     * @return An Organizer entity ready for MongoDB persistence.
     */
    public static Organizer mapOrganizerToEntity(OrganizerDTO dto) {

        if (dto == null) {
            return null;
        }

        Organizer entity = new Organizer();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setName(dto.getName());
        entity.setVatNumber(dto.getVatNumber());
        entity.setEmail(dto.getEmail());

        List<EventOrganizationDTO> eventDto = dto.getEvents();
        if (eventDto != null) {
            List<EventOrganization> events = new ArrayList<>();
            for (EventOrganizationDTO evDto : eventDto) {
                EventOrganization ev = new EventOrganization(
                        evDto.getId(),
                        evDto.getName()
                );
                events.add(ev);
            }
            entity.setEvents(events);
        } else {
            entity.setEvents(new ArrayList<>());
        }

        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPassword(dto.getPassword());

        List<OrganizationForLinkingDTO> organizationsDto = dto.getOrganizations();
        if (organizationsDto != null) {

            List<OrganizationForLinking> organizations = new ArrayList<>();
            for (OrganizationForLinkingDTO orgDto : organizationsDto) {
                OrganizationForLinking org = new OrganizationForLinking(
                    orgDto.getId(),
                    orgDto.getName()
                );
                organizations.add(org);
            }
            entity.setOrganizations(organizations);

        } else {
            entity.setOrganizations(new ArrayList<>());
        }
        return entity;
    }

    /**
     * Converts an Organizer Entity (Database object) into an OrganizerDTO (API object).
     *
     * @param entity The Organizer entity retrieved from MongoDB.
     * @return An OrganizerDTO populated with data.
     */
    public static OrganizerDTO mapOrganizerToDto(Organizer entity) {
        if  (entity == null) {
            return null;
        }

        OrganizerDTO dto = new OrganizerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVatNumber(entity.getVatNumber());
        dto.setEmail(entity.getEmail());
        dto.setType(entity.getType());

        List<EventOrganization> eventsEntity = entity.getEvents();
        if (eventsEntity != null) {
            List<EventOrganizationDTO> events = new ArrayList<>();
            for (EventOrganization ev : eventsEntity) {
                events.add(
                        new EventOrganizationDTO(
                                ev.getId(),
                                ev.getName()
                        )
                );
            }
            dto.setEvents(events);
        } else {
            dto.setEvents(new ArrayList<>());
        }

        dto.setLastName(entity.getLastName());
        dto.setUsername(entity.getUsername());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPassword(entity.getPassword());

        List<OrganizationForLinking> organizationLinkingentity =  entity.getOrganizations();
        if (organizationLinkingentity != null) {
            List<OrganizationForLinkingDTO> organizations = new ArrayList<>();
            for (OrganizationForLinking org : organizationLinkingentity) {
                OrganizationForLinkingDTO orgDto = new OrganizationForLinkingDTO(
                        org.getId(),
                        org.getName()
                );
                organizations.add(orgDto);
            }
            dto.setOrganizations(organizations);
        } else {
            dto.setOrganizations(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Converts an OrganizationDTO (API object) into an Organization Entity (Database object).
     * <p>
     * Maps basic fields and event lists, initializing members and pending requests as empty lists.
     * </p>
     *
     * @param organizationDto The OrganizationDTO received from the client.
     * @return An Organization entity ready for persistence.
     */
    public static Organization mapOrganizationToEntity(OrganizationDTO organizationDto) {
        if (organizationDto == null) {
            return null;
        }

        Organization entity = new Organization();
        entity.setId(organizationDto.getId());
        entity.setType(organizationDto.getType());
        entity.setName(organizationDto.getName());
        entity.setVatNumber(organizationDto.getVatNumber());
        entity.setEmail(organizationDto.getEmail());

        List<EventOrganizationDTO> eventDto = organizationDto.getEvents();
        if (eventDto != null) {
            List<EventOrganization> events = new ArrayList<>();
            for (EventOrganizationDTO evDto : eventDto) {
                EventOrganization ev = new EventOrganization(
                        evDto.getId(),
                        evDto.getName()
                );
                events.add(ev);
            }
            entity.setEvents(events);
        } else {
            entity.setEvents(new ArrayList<>());
        }

        entity.setMembers(List.of());
        entity.setPendingRequests(List.of());
        return entity;
    }

    /**
     * Converts an Organization Entity (Database object) into an OrganizationDTO (API object).
     * <p>
     * Populates the DTO with organization details, including members and pending requests.
     * </p>
     *
     * @param entity The Organization entity retrieved from MongoDB.
     * @return An OrganizationDTO populated with data.
     */
    public static OrganizationDTO mapOrganizationToDto(Organization entity) {

        if (entity == null) {
            return null;
        }

        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setName(entity.getName());
        dto.setVatNumber(entity.getVatNumber());
        dto.setEmail(entity.getEmail());

        List<EventOrganization> eventDto = entity.getEvents();
        if (eventDto != null) {
            List<EventOrganizationDTO> events = new ArrayList<>();
            for (EventOrganization ev : eventDto) {
                EventOrganizationDTO evDto = new EventOrganizationDTO(
                        ev.getId(),
                        ev.getName()
                );
                events.add(evDto);
            }
            dto.setEvents(events);
        } else  {
            dto.setEvents(new ArrayList<>());
        }

        List<Members>  membersDto = entity.getMembers();
        if (membersDto != null) {
            List<MembersDTO> members = new ArrayList<>();
            for (Members member : membersDto) {
                MembersDTO memberDto = new MembersDTO();
                memberDto.setId(member.getId());
                memberDto.setName(member.getName());
                members.add(memberDto);
            }
            dto.setMembers(members);
        } else {
            dto.setMembers(new ArrayList<>());
        }

        List<Request> pendingRequests = entity.getPendingRequests();
        if  (pendingRequests != null) {
            List<RequestDTO> requests = new ArrayList<>();
            for (Request req : pendingRequests) {
                RequestDTO requestDto = new RequestDTO();
                requestDto.setId(req.getId());
                requestDto.setUsername(req.getUsername());
                requests.add(requestDto);
            }
            dto.setPendingRequests(requests);
        } else {
            dto.setPendingRequests(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Converts a UserDTO into a UserNode for the Graph Database (Neo4j).
     * <p>
     * Extracts minimal info required for the graph: ID, Username, and Coordinates.
     * </p>
     *
     * @param userDto The User DTO source.
     * @return A UserNode entity for Neo4j.
     */
    public static UserNode mapUserToNode(UserDTO userDto) {

        if (userDto == null)
            return null;

        UserNode userNode = new UserNode();
        userNode.setId(userDto.getId());
        userNode.setUsername(userDto.getUsername());

        AddressDTO homeTown = userDto.getHomeTown();
        if (homeTown != null && homeTown.getLoc() != null) {
            userNode.setCoordinates(homeTown.getLoc().getCoordinates());
        } else {
            userNode.setCoordinates(null);
        }

        return userNode;
    }

    /**
     * Converts an AbstractOrganizerDTO (Organizer or Organization) into an OrganizerNode for Neo4j.
     * <p>
     * Handles the logic to determine the correct "username" or display name to use in the graph
     * based on whether the input is an individual Organizer or an Organization.
     * </p>
     *
     * @param organizer The source DTO (can be OrganizerDTO or OrganizationDTO).
     * @return An OrganizerNode entity for Neo4j.
     */
    public static OrganizerNode mapOrganizerToNode(AbstracOrganizer organizer) {
        if (organizer == null)
            return null;

        OrganizerNode organizerNode = new OrganizerNode();
        organizerNode.setId(organizer.getId());

        if (organizer instanceof Organizer) {
            organizerNode.setUsername(((Organizer) organizer).getUsername());
        } else if (organizer instanceof Organization) {
            organizerNode.setUsername(organizer.getName());
        }

        return organizerNode;
    }

    public Mapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Event mapEventToEntity(EventDTO eventDTO) {
        if (eventDTO == null)
            return null;

        Event entity = new Event();
        entity.setId(eventDTO.getId());
        entity.setEventName(eventDTO.getEventName());
        entity.setStartingDate(eventDTO.getStartingDate());
        entity.setEndingDate(eventDTO.getEndingDate());
        entity.setDescription(eventDTO.getDescription());
        entity.setUrlImg(eventDTO.getUrlImg());
        entity.setTotalReview(eventDTO.getTotalReview());
        entity.setEventScore(eventDTO.getEventScore());
        Map<String, Double> ticketPriceDTO = eventDTO.getTicketPrice();

        if (ticketPriceDTO != null) {
            entity.setTicketPrice(Map.copyOf(ticketPriceDTO));
        } else {
            entity.setTicketPrice(new HashMap<>());
        }

        List<String> categoriesDTO = eventDTO.getCategories();
        if  (categoriesDTO != null) {
            entity.setCategories(List.copyOf(categoriesDTO));
        } else {
            entity.setCategories(new ArrayList<>());
        }

        Object startingTimesDTO = eventDTO.getStartingTimes();
        if (startingTimesDTO != null) {
            entity.setStartingTimes(startingTimesDTO);
        } else {
            entity.setStartingTimes(new HashMap<>());
        }

        AddressDTO addressDTO = eventDTO.getPosition();
        if (addressDTO != null) {
            Address address = new Address();
            address.setCityName(addressDTO.getCityName());
            address.setFullAddress(addressDTO.getFullAddress());
            LocationDTO locationDTO = addressDTO.getLoc();
            if (locationDTO != null) {
                Location location = new Location(
                        locationDTO.getType(),
                        locationDTO.getCoordinates()
                );
                address.setLocation(location);
            } else {
                address.setLocation(new Location());
            }
            entity.setPosition(address);
        } else {
            entity.setPosition(new Address());
        }

        List<ReviewDTO> reviewsDTO = eventDTO.getReviews();
        if (reviewsDTO != null) {
            List<Review> reviews = new ArrayList<>();
            for (ReviewDTO reviewDTO : reviewsDTO) {
                Review review = new Review();
                review.setScore(reviewDTO.getScore());
                review.setText(reviewDTO.getText());
                review.setUsername(reviewDTO.getUsername());
                reviews.add(review);
            }
            entity.setReviews(reviews);
        } else {
            entity.setReviews(new ArrayList<>());
        }

        List<AttendentDTO> attendeesDTO =  eventDTO.getAttendees();
        if (attendeesDTO != null) {
            List<Attendent> attendees = new ArrayList<>();
            for (AttendentDTO attendeeDTO : attendeesDTO) {
                Attendent attendee = new Attendent();
                attendee.setId(attendeeDTO.getId());
                attendee.setTicketType(attendeeDTO.getTicketType());
                attendee.setEmail(attendeeDTO.getEmail());
                attendee.setUsername(attendeeDTO.getUsername());
                attendee.setHomeTown(attendeeDTO.getHomeTown());
                attendee.setDateOfBirth(attendeeDTO.getDateOfBirth());
                attendees.add(attendee);
            }
            entity.setAttendees(attendees);
        } else {
            entity.setAttendees(new ArrayList<>());
        }

        StatisticsDTO statisticsDTO = eventDTO.getStatistic();
        if (statisticsDTO != null) {
            Statistics statistics = new Statistics();
            statistics.setDateUpdate(statisticsDTO.getDateUpdate());
            statistics.setAverageAge(statisticsDTO.getAverageAge());
            statistics.setPredictedIncome(statisticsDTO.getPredictedIncome());
            statistics.setOriginAttenders(Map.copyOf(statisticsDTO.getOriginAttenders()));
            statistics.setTotalAttenders(statisticsDTO.getTotalAttenders());
            entity.setStatistics(statistics);
        } else {
            entity.setStatistics(null);
        }

        return entity;
    }

    public static EventNode mapEventToNode(Event entity) {
        EventNode eventNode = new EventNode();
        eventNode.setId(entity.getId());
        eventNode.setEventName(entity.getEventName());
        eventNode.setStartingDate(entity.getStartingDate());
        eventNode.setEndingDate(entity.getEndingDate());
        if (entity.getPosition() != null && entity.getPosition().getLocation() != null)
            eventNode.setCoordinates(entity.getPosition().getLocation().getCoordinates());
        else
            eventNode.setCoordinates(null);
        return eventNode;
    }

    public static EventDTO mapEventToDTO(Event entity) {
        if (entity == null) {
            return null;
        }

        EventDTO eventDTO = new EventDTO();

        eventDTO.setId(entity.getId());
        eventDTO.setEventName(entity.getEventName());
        eventDTO.setStartingDate(entity.getStartingDate());
        eventDTO.setEndingDate(entity.getEndingDate());
        eventDTO.setDescription(entity.getDescription());
        eventDTO.setUrlImg(entity.getUrlImg());
        eventDTO.setTotalReview(entity.getTotalReview());
        eventDTO.setEventScore(entity.getEventScore());

        List<String> categories = entity.getCategories();
        if (categories != null) {
            eventDTO.setCategories(List.copyOf(categories));
        } else {
            eventDTO.setCategories(new ArrayList<>());
        }

        Map<String, Double> ticketPrice = entity.getTicketPrice();
        if (ticketPrice != null) {
            eventDTO.setTicketPrice(Map.copyOf(ticketPrice));
        }  else {
            eventDTO.setTicketPrice(new HashMap<>());
        }

        Object startingTimes = entity.getStartingTimes();
        if (startingTimes != null) {
            eventDTO.setStartingTimes(startingTimes);
        } else {
            eventDTO.setStartingTimes(new HashMap<>());
        }

        Address position = entity.getPosition();
        if (position != null) {
            AddressDTO positionDTO = new AddressDTO();
            positionDTO.setCityName(position.getCityName());
            positionDTO.setFullAddress(position.getFullAddress());
            Location location =  position.getLocation();
            if (location != null) {
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setType(location.getType());
                locationDTO.setCoordinates(location.getCoordinates());
                positionDTO.setLoc(locationDTO);
            } else {
                positionDTO.setLoc(new LocationDTO());
            }
            eventDTO.setPosition(positionDTO);
        } else {
            eventDTO.setPosition(new AddressDTO());
        }

        List<Review>  reviews = entity.getReviews();
        if (reviews != null) {
            List<ReviewDTO> reviewDTOs = new ArrayList<>();
            for (Review review : reviews) {
                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setScore(review.getScore());
                reviewDTO.setUsername(review.getUsername());
                reviewDTO.setText(review.getText());
                reviewDTOs.add(reviewDTO);
            }
            eventDTO.setReviews(reviewDTOs);
        } else {
            eventDTO.setReviews(new ArrayList<>());
        }

        Statistics statistics = entity.getStatistics();
        if (statistics != null) {
            StatisticsDTO statisticsDTO = new StatisticsDTO();
            statisticsDTO.setDateUpdate(statistics.getDateUpdate());
            statisticsDTO.setAverageAge(statistics.getAverageAge());
            statisticsDTO.setPredictedIncome(statistics.getPredictedIncome());
            statisticsDTO.setOriginAttenders(Map.copyOf(statistics.getOriginAttenders()));
            statisticsDTO.setTotalAttenders(statistics.getTotalAttenders());
            eventDTO.setStatistic(statisticsDTO);
        } else {
            eventDTO.setStatistic(null);
        }

        return  eventDTO;
    }
}
