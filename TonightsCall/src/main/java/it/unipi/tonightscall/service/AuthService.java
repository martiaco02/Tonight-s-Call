package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.OrganizerDTO;
import it.unipi.tonightscall.DTO.UserDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.graph.OrganizerNode;
import it.unipi.tonightscall.entity.graph.TopicNode;
import it.unipi.tonightscall.entity.graph.UserNode;
import it.unipi.tonightscall.jwt.JWTService;
import it.unipi.tonightscall.repository.document.AbstractOrganizerRepository;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.repository.graph.OrganizerGraphRepository;
import it.unipi.tonightscall.repository.graph.TopicGraphRepository;
import it.unipi.tonightscall.repository.graph.UserGraphRepository;
import it.unipi.tonightscall.utilies.Mapper;
import it.unipi.tonightscall.utilies.Roles;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling authentication logic.
 * <p>
 * This service manages the core business logic for user registration and login,
 * interacting with the repository for data persistence, the password encoder for security,
 * and the JWT service for token generation.
 * </p>
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final OrganizationRepository organizationRepository;

    private final UserGraphRepository userGraphRepository;
    private final OrganizerGraphRepository  organizerGraphRepository;
    private final TopicGraphRepository topicGraphRepository;

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository,
                       OrganizerRepository organizerRepository, OrganizationRepository organizationRepository,
                       UserGraphRepository userGraphRepository,
                       OrganizerGraphRepository organizerGraphRepository,
                       TopicGraphRepository topicGraphRepository,
                       PasswordEncoder passwordEncoder,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.organizerRepository = organizerRepository;
        this.userGraphRepository = userGraphRepository;
        this.organizerGraphRepository = organizerGraphRepository;
        this.topicGraphRepository = topicGraphRepository;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * Checks if the username is already taken. If not, encodes the password,
     * maps the DTO to an entity, and saves it to the database.
     * </p>
     *
     * @param userDto The data transfer object containing user details.
     * @return The DTO of the newly created user (without the password).
     * @throws RuntimeException If the username already exists or the data are not consistent.
     */
    @Transactional
    public UserDTO registerUser(UserDTO userDto) {

        if (userDto.getUsername() == null)
            throw new RuntimeException("Username is required!");

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("The username " + userDto.getUsername() + " is already taken.");
        }

        if (userDto.getPassword() == null)
            throw new RuntimeException("Password is required!");

        if (userDto.getEmail() == null)
            throw new RuntimeException("Email is required!");

        if (userDto.getInterests() == null ||  userDto.getInterests().isEmpty())
            throw new RuntimeException("At least one interest is required!");

        if (userDto.getHomeTown() == null)
            throw new RuntimeException("HomeTown is required!");

        userDto.setReviewedEvents(new ArrayList<>());
        userDto.setFriends(new ArrayList<>());

        User entity = Mapper.mapUserToEntity(userDto);
        entity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User saved = userRepository.save(entity);

        UserNode userNode = Mapper.mapUserToNode(userDto);
        userNode.setId(saved.getId());

        if (userDto.getInterests() != null && !userDto.getInterests().isEmpty()) {

            for (String interess : userDto.getInterests()) {

                interess = interess.toUpperCase();
                TopicNode topicNode = topicGraphRepository.findById(interess)
                    .orElse(new TopicNode(interess));

                topicNode = topicGraphRepository.save(topicNode);
                userNode.getInterests().add(topicNode);
            }
        }

        UserNode savedNode = userGraphRepository.save(userNode);
        return Mapper.mapUserToDto(saved);
     }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param username    The username provided by the client.
     * @param rawPassword The plain text password provided by the client.
     * @return A valid JWT token string.
     * @throws RuntimeException If the user is not found or the password does not match.
     */
    public String loginUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Not Found!"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }

        return jwtService.generateToken(user.getUsername(), Roles.USER_ROLE);
    }

    /**
     * Registers a new Organizer (Individual).
     * <p>
     * Saves the entity in MongoDB and creates a corresponding node in the Graph Database (Neo4j).
     * </p>
     *
     * @param organizerDto The details of the organizer to register.
     * @return The registered OrganizerDTO.
     */
    @Transactional
    public OrganizerDTO registerOrganizer(OrganizerDTO organizerDto) {
        if (organizerRepository.existsByUsername(organizerDto.getUsername())) {
            throw new RuntimeException("The username " + organizerDto.getUsername() + " is already taken.");
        }

        Organizer entity = Mapper.mapOrganizerToEntity(organizerDto);
        entity.setPassword(passwordEncoder.encode(organizerDto.getPassword()));
        Organizer saved = organizerRepository.save(entity);

        OrganizerNode organizerNode = Mapper.mapOrganizerToNode(entity);
        OrganizerNode savedNode = organizerGraphRepository.save(organizerNode);

        saved.setPassword(null);
        return Mapper.mapOrganizerToDto(saved);
    }

    /**
     * Authenticates an Organizer.
     *
     * @param username    The username of the organizer.
     * @param rawPassword The password.
     * @return JWT Token with ORGANIZER_ROLE.
     */
    public String loginOrganizer(String username, String rawPassword) {
        AbstracOrganizer organizer = organizerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organization Not Found!"));

        if (!(organizer instanceof Organizer organizer1))
            throw new RuntimeException("The username " + username + " is already taken and not by an organizer");

        if (!passwordEncoder.matches(rawPassword, organizer1.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }

        return jwtService.generateToken(organizer1.getUsername(), Roles.ORGANIZER_ROLE);
    }

    /**
     * Authenticates an Organization via one of its members.
     * <p>
     * Since Organizations don't have a single password, this method checks if the provided
     * password matches ANY of the members' passwords within that organization.
     * </p>
     *
     * @param username The name of the Organization.
     * @param password The password of a member attempting to login on behalf of the org.
     * @return JWT Token with ORGANIZATION_ROLE.
     */
    public String loginOrganization(String username, String password) {
        AbstracOrganizer organization = organizationRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Organization Not Found!"));

        if (!(organization instanceof Organization organization1))
            throw new RuntimeException("The username " + username + " is already taken and not by an organization");

        List<Members> members = organization1.getMembers();
        for (Members member : members) {
            if (passwordEncoder.matches(password, member.getPassword())) {
                return jwtService.generateToken(organization1.getName(), Roles.ORGANIZATION_ROLE);
            }
        }
        throw new RuntimeException("Wrong password!");
    }
}