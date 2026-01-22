package it.unipi.tonightscall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unipi.tonightscall.DTO.*;
import it.unipi.tonightscall.service.AuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling authentication operations.
 * <p>
 * This controller exposes endpoints for user registration and login (authentication).
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user in the system.
     *
     * @param userDto The user information transfer object containing registration details.
     * @return The created UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(summary = "Register a new User", description = "Creates a new user account in the system with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or User already exists",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("user/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDto) {
        try {
            UserDTO createdUser = authService.registerUser(userDto);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Authenticates a user and issues a token.
     *
     * @param loginRequest The login credentials (username and password).
     * @return An AuthResponseDTO containing the JWT token if successful, or an error message if unauthorized.
     */
    @Operation(summary = "User Login", description = "Authenticates a user using username and password and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful, token returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials (username or password incorrect)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("user/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String token = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new AuthResponseDTO(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Login fallito: " + e.getMessage());
        }
    }


    @PostMapping("organizer/register")
    public ResponseEntity<?> registerOrganizer(@RequestBody OrganizerDTO organizerDto) {
        try {
            OrganizerDTO createdOrganizer = authService.registerOrganizer(organizerDto);
            return ResponseEntity.ok(createdOrganizer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("organizer/login")
    public ResponseEntity<?> loginOrganizer(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String token = authService.loginOrganizer(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new AuthResponseDTO(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Login fallito: " + e.getMessage());
        }
    }

    @PostMapping("organization/register")
    public ResponseEntity<?> registerOrganization(@RequestBody OrganizationDTO organizationDto) {
        try {
            OrganizerDTO createdOrganization = authService.registerOrganization(organizationDto);
            return ResponseEntity.ok(createdOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}