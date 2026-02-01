package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.DTO.OrganizerDTO;
import it.unipi.tonightscall.DTO.StatisticsDTO;
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
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public final int PAGE_SIZE = 5;

    public OrganizerService(
            OrganizationRepository organizationRepository,
            OrganizerRepository organizerRepository,
            EventRepository eventRepository,
            OrganizerGraphRepository organizerGraphRepository,
            EventGraphRepository eventGraphRepository,
            TopicGraphRepository topicGraphRepository,
            PasswordEncoder passwordEncoder) {
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
        this.organizerGraphRepository = organizerGraphRepository;
        this.organizerRepository = organizerRepository;
        this.eventGraphRepository = eventGraphRepository;
        this.topicGraphRepository = topicGraphRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns all the organizers
     *
     * @param pageable for implementing the pagination
     * @return Page of OrganizerDTO or null
     */
    public Page<@NonNull OrganizerDTO> getAllOrganizers(Pageable pageable) {
        Page<@NonNull Organizer> organizer = this.organizerRepository.findByType("ORGANIZER", pageable);
        if (organizer.isEmpty()) {
            return null;
        }

        return organizer.map(Mapper::mapOrganizerToDto);
    }

    /**
     * Returns the Organizer from the id
     *
     * @param id The id of the organizer
     * @return The wanted organizer or null
     */
    public OrganizerDTO getOrganizerById(String id) {
        Organizer org = this.organizerRepository.findById(id).orElse(null);
        return org == null ? null : Mapper.mapOrganizerToDto(org);
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
     * @param id The id of the authenticated Organizer (creator).
     * @return The created OrganizationDTO.
     * @throws RuntimeException If the organization name is taken or the organizer is not found.
     */
    @Transactional
    public OrganizationDTO registerOrganization(OrganizationDTO organizationDTO, String id) {

        if (organizationDTO.getName() == null)
            throw new RuntimeException("Organization name is required");

        if (organizationRepository.existsByName(organizationDTO.getName())) {
            throw new RuntimeException("The name " + organizationDTO.getName() + " is already taken.");
        }

        Organizer me = organizerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));


        Organization organization = Mapper.mapOrganizationToEntity(organizationDTO);
        Members members = new Members();
        members.setPassword(me.getPassword());
        members.setId(me.getId());
        members.setName(me.getUsername());

        List<Members> membersList = new ArrayList<>();
        membersList.add(members);
        organization.setMembers(membersList);
        Organization saved = organizationRepository.save(organization);

        OrganizerNode organizerNode = Mapper.mapOrganizerToNode(saved);

        me.getOrganizations().add(
                new OrganizationForLinking(saved.getId(), saved.getName())
        );

        organizerRepository.save(me);
        organizerGraphRepository.save(organizerNode);

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
     * @param id Id of the organizer
     * @return The created EventDTO
     * @throws RuntimeException If the organization name is not found or if the organizationNode is not found or if the data are not consistent
     */
    @Transactional
    public EventDTO registerEvent(EventDTO eventDTO, String id, String role) {

        AbstracOrganizer me;
        if (role.equals(Roles.ORGANIZER_ROLE)) {

            me = organizerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));
        } else if (role.equals(Roles.ORGANIZATION_ROLE)) {
            me = organizationRepository.findById(id)
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
        eventDTO.setTotalReview(0);
        eventDTO.setEventScore(0.0);
        Event event = Mapper.mapEventToEntity(eventDTO);

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


    /**
     * Updates and Organizer data
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the Organizer exists.</li>
     *      <li>Checks for consistency: username and date of birth can't be changed</li>
     *      <li>Updates the specific data for the organizer</li>
     *      <li>Updates the Organizations data if the Organizer is part of some</li>
     *      <li>Saves the updated Organizer to MongoDB.</li>
     *      <li>Updates the corresponding node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param id The id of the Organizer to update.
     * @param newOrganizerDTO The OrganizerDTO with the updated data.
     * @return The updated OrganizerDTO.
     * @throws RuntimeException If the organizer is not found or if the user tries to update username or password.
     */
    @Transactional
    public OrganizerDTO updateOrganizer(String id, OrganizerDTO newOrganizerDTO){
        Organizer oldOrganizer = organizerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));

        // checking consistency: username can't change
        if(newOrganizerDTO.getUsername() != null && !oldOrganizer.getUsername().equals(newOrganizerDTO.getUsername())){
            throw new RuntimeException("Can't change username!");
        }

        // checking consistency: date of birth can't change
        if(newOrganizerDTO.getDateOfBirth() != null && !oldOrganizer.getDateOfBirth().isEqual(newOrganizerDTO.getDateOfBirth())){
            throw new RuntimeException("Can't change date of birth!");
        }

        boolean updateName = false;
        boolean updatePsw = false;
        // updating the new data
        if(newOrganizerDTO.getName() != null){
            updateName = true;
            oldOrganizer.setName(newOrganizerDTO.getName());
        }
        if(newOrganizerDTO.getLastName() != null){oldOrganizer.setLastName(newOrganizerDTO.getLastName());}
        if(newOrganizerDTO.getPassword() != null){
            updatePsw = true;
            oldOrganizer.setPassword(passwordEncoder.encode(newOrganizerDTO.getPassword()));
        }
        if(newOrganizerDTO.getVatNumber() != null){ oldOrganizer.setVatNumber(newOrganizerDTO.getVatNumber());}
        if(newOrganizerDTO.getEmail() != null){ oldOrganizer.setEmail(newOrganizerDTO.getEmail());}

        //if the name and psw changed we need to propagate it to organizer's organizations members list
        if((updateName || updatePsw) && oldOrganizer.getOrganizations() != null){
            for(int i=0; i<oldOrganizer.getOrganizations().size(); i++){
                Organization org = organizationRepository.findById(oldOrganizer.getOrganizations().get(i).getId())
                        .orElseThrow(() -> new RuntimeException("Organization Not Found!"));
                // searching for the specific member in the organization and updating the data
                for(int j=0; j<org.getMembers().size(); j++){
                    if(org.getMembers().get(j).getId().equals(oldOrganizer.getId())){
                        if(updateName){
                            org.getMembers().get(j).setName(newOrganizerDTO.getName());
                        }
                        if(updatePsw){
                            org.getMembers().get(j).setPassword(newOrganizerDTO.getPassword());
                        }
                        break;
                    }
                }
                //updating document
                organizationRepository.save(org);

            }

        }
        // updating document
        organizerRepository.save(oldOrganizer);

        return Mapper.mapOrganizerToDto(oldOrganizer);
    }

    /**
     * Deletes the membership of an Organizer to an Organization
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the Organizer exists.</li>
     *      <li>Checks if the Organization exists.</li>
     *      <li>Deletes the Organization from the Organizer's organizations.</li>
     *      <li>Deletes the Organizer (Member) from the Organization's members.</li>
     *      <li>Saves the updated Organizer to MongoDB.</li>
     * </ol>
     * </p>
     *
     * @param id The id of the Organizer whose membership must be deleted.
     * @param orgName The name of the Organization from whom the Organizer wants to delete the membership.
     * @return The updated OrganizerDTO
     * @throws RuntimeException If the organizer is not found or if the Organization is not found.
     */
    @Transactional
    public OrganizerDTO deleteOrganizationMembership(String id, String orgName){
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));

        Organization organization = organizationRepository.findByName(orgName)
                .orElseThrow(() -> new RuntimeException("Organization Not Found!"));

        if (organizer.getOrganizations() != null) {
            boolean removed = organizer.getOrganizations().removeIf(
                    org -> org.getName().equals(orgName)
            );
            if (removed) {
                organizerRepository.save(organizer);
            }
        }

        if (organization.getMembers() != null) {
            boolean removed = organization.getMembers().removeIf(
                    member -> member.getId().equals(organizer.getId())
            );
            if (removed) {
                organizationRepository.save(organization);
            }
        }

        return Mapper.mapOrganizerToDto(organizer);
    }

}

