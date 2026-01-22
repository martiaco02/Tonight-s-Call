package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.entity.document.Members;
import it.unipi.tonightscall.entity.document.Organization;
import it.unipi.tonightscall.entity.document.Organizer;
import it.unipi.tonightscall.entity.graph.OrganizerNode;
import it.unipi.tonightscall.jwt.JWTService;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.repository.graph.OrganizerGraphRepository;
import it.unipi.tonightscall.utilies.Mapper;
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
public class ControllerService {

    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;

    private final OrganizerGraphRepository organizerGraphRepository;


    public ControllerService(
            OrganizationRepository organizationRepository,
            OrganizerRepository organizerRepository,
            OrganizerGraphRepository organizerGraphRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizerGraphRepository = organizerGraphRepository;
        this.organizerRepository = organizerRepository;
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

        OrganizerNode organizerNode = Mapper.mapOrganizerToNode(organizationDTO);
        OrganizerNode savedNode = organizerGraphRepository.save(organizerNode);

        return Mapper.mapOrganizationToDto(saved);

    }
}
