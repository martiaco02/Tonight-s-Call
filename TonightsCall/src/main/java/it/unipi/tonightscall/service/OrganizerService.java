package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.graph.EventNode;
import it.unipi.tonightscall.entity.graph.OrganizerNode;
import it.unipi.tonightscall.entity.graph.TopicNode;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import it.unipi.tonightscall.repository.graph.OrganizerGraphRepository;
import it.unipi.tonightscall.repository.graph.TopicGraphRepository;
import it.unipi.tonightscall.utilies.Mapper;
import it.unipi.tonightscall.utilies.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling complex business logic for Controllers.
 * <p>
 * Unlike AuthService which handles authentication, this service manages
 * operations requiring interaction between different entities (e.g., an Organizer creating an Organization).
 * </p>
 */
@Service
public class OrganizerService {

    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;

    private final OrganizerGraphRepository organizerGraphRepository;
    private final EventGraphRepository  eventGraphRepository;
    private final TopicGraphRepository topicGraphRepository;

    public OrganizerService(
            OrganizationRepository organizationRepository,
            OrganizerRepository organizerRepository,
            EventRepository eventRepository,
            OrganizerGraphRepository organizerGraphRepository,
            EventGraphRepository eventGraphRepository,
            TopicGraphRepository topicGraphRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
        this.organizerGraphRepository = organizerGraphRepository;
        this.organizerRepository = organizerRepository;
        this.eventGraphRepository = eventGraphRepository;
        this.topicGraphRepository = topicGraphRepository;
    }

    /**
     * Registers a new Organization linked to an existing Organizer.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the organization name is unique.</li>
     *      <li>Retrieves the creating Organizer from the database.</li>
     *      <li>Creates the Organization entity and adds the creator as the first member.</li>
     *      <li>Saves the Organization to MongoDB.</li>
     *      <li>Creates and saves the corresponding node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param organizationDTO The details of the organization to create.
     * @param username        The username of the authenticated Organizer (creator).
     * @return The created OrganizationDTO.
     * @throws RuntimeException If the organization name is taken or the organizer is not found.
     */
    @Transactional
    public OrganizationDTO registerOrganization(OrganizationDTO organizationDTO, String username) {

        if (organizationDTO.getName() == null)
            throw new RuntimeException("Organization name is required");

        if (organizationRepository.existsByName(organizationDTO.getName())) {
            throw new RuntimeException("The name " + organizationDTO.getName() + " is already taken.");
        }

        Organizer me = organizerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));


        Organization organization = Mapper.mapOrganizationToEntity(organizationDTO);
        Members members = new Members();
        members.setPassword(me.getPassword());
        members.setId(me.getId());
        members.setName(username);

        List<Members> membersList = new ArrayList<>();
        membersList.add(members);
        organization.setMembers(membersList);
        Organization saved = organizationRepository.save(organization);

        OrganizerNode organizerNode = Mapper.mapOrganizerToNode(saved);

        me.getOrganizations().add(
                new OrganizationForLinking(saved.getId(), saved.getName())
        );

        organizerRepository.save(me);
        OrganizerNode savedNode = organizerGraphRepository.save(organizerNode);

        return Mapper.mapOrganizationToDto(saved);

    }

    /**
     * Register a new Event
     * <p>
     * This method:
     * <ol>
     *     <li>Create the Document for the Event</li>
     *     <li>Add the Node for the event</li>
     *     <li>Create the Relationship with the Organizer</li>
     *     <li>Create the Relationship with the Topics (creating the nodes whenever they don't exist)</li>
     * </ol>
     * </p>
     * @param eventDTO The Event's details
     * @param username Username of the organizer
     * @return The created EventDTO
     * @throws RuntimeException If the organization name is not found or if the organizationNode is not found or if the data are not consistent
     */
    @Transactional
    public EventDTO registerEvent(EventDTO eventDTO, String username, String role) {

        AbstracOrganizer me;
        if (role.equals(Roles.ORGANIZER_ROLE)) {

            me = organizerRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));
        } else if (role.equals(Roles.ORGANIZATION_ROLE)) {
            me = organizationRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));
        } else {
            throw new RuntimeException("Invalid role");
        }

        if (eventDTO.getEventName() == null || eventDTO.getEventName().isEmpty() || eventDTO.getEventName().isBlank()) {
            throw new RuntimeException("Event Name is empty");
        }

        if (eventDTO.getPosition() == null) {
            throw new RuntimeException("Position is empty");
        }

        if (eventDTO.getStartingDate() == null) {
            throw new RuntimeException("Starting Date is empty");
        }

        if (eventDTO.getEndingDate() != null) {
            if (!eventDTO.getStartingDate().isBefore(eventDTO.getEndingDate())) {
                throw new RuntimeException("Ending Date is before Start Date");
            }
        }

        Event event = Mapper.mapEventToEntity(eventDTO);
        event.setTotalReview(0);
        event.setEventScore(0);

        Event saved  = eventRepository.save(event);

        me.getEvents().add(
                new EventOrganization(
                        saved.getId(),
                        saved.getEventName()
                )
        );

        if (role.equals(Roles.ORGANIZER_ROLE)) {

            organizerRepository.save((Organizer) me);
        } else {
            organizationRepository.save((Organization) me);
        }

        EventNode eventNode = Mapper.mapEventToNode(saved);

        OrganizerNode organizerNode = organizerGraphRepository.findById(me.getId())
                .orElseThrow(() -> new RuntimeException("Organizer Node Not Found!"));

        organizerNode.getOrganized().add(eventNode);
        organizerGraphRepository.save(organizerNode);

        for (String category : eventDTO.getCategories()) {
            category = category.toUpperCase();
            TopicNode categoryNode = topicGraphRepository.findById(category)
                    .orElse(new TopicNode(category));

            eventNode.getCategories().add(categoryNode);
            topicGraphRepository.save(categoryNode);
        }

        eventGraphRepository.save(eventNode);

        return Mapper.mapEventToDTO(saved);
    }
}
