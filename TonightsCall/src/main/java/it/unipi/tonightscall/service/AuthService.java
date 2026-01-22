package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.UserDTO;
import it.unipi.tonightscall.entity.User;
import it.unipi.tonightscall.jwt.JWTService;
import it.unipi.tonightscall.repository.UserRepository;
import it.unipi.tonightscall.utilies.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
     * @throws RuntimeException If the username already exists.
     */
    public UserDTO register(UserDTO userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("The username " + userDto.getUsername() + " is already taken.");
        }

        User entity = Mapper.mapUserToEntity(userDto);
        entity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User saved = userRepository.save(entity);

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
    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        return jwtService.generateToken(user.getUsername());
    }
}