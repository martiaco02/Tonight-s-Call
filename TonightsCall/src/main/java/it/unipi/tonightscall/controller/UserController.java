package it.unipi.tonightscall.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unipi.tonightscall.DTO.*;
import it.unipi.tonightscall.entity.document.User;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller handling User operations.
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new friendship in the system.
     *
     * @param payload The user information transfer object containing registration details.
     * @param authentication Security context of the user performing the action.
     * @return The updated UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Registers a new friendship",
            description = "Registers a new friendship between two users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friendship registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or authenticated user or requested friend are not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/RegisterFriend")
    public ResponseEntity<?> registerFriend(@RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            UserDTO updatedUser = userService.addFriendship(payload.get("username"), authentication.getName());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<User> getAllUsers() { return this.userService.getAllUsers(); }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) { return this.userService.getUserById(id); }


    /**
     * Registers a new attendance in the system.
     *
     * @param payload The user information transfer object containing registration details.
     * @param authentication Security context of the user performing the action.
     * @return The updated EventDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Registers a User attendance to an Event",
            description = "Adds a user new attendance to an Event attendees' list."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Membership removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, User or Event are not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/attending")
    public ResponseEntity<?> attending(@RequestBody Map<String, String> payload, Authentication authentication) {
        try{
            EventDTO updatedEvent = userService.addAttendance(payload.get("event_id"), payload.get("ticket_type"), authentication.getName());
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registers a new Review in the system.
     *
     * @param parameters The new ReviewParameterDTO containing the new review data.
     * @param authentication Security context of the user performing the action.
     * @return The updated EventDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Creates a new review",
            description = "Creates a new user's review of an event"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Review created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, Event or User are not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/review")
    public ResponseEntity<?> review(@RequestBody ReviewParameterDTO parameters, Authentication authentication) {
        try {
            EventDTO updatedEvent = userService.addReview(parameters.getEventId(), parameters.getText(), parameters.getScore(), authentication.getName());
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates a User data in the system.
     *
     * @param user The new UserDTO containing the updated user's data.
     * @param authentication Security context of the user performing the action.
     * @return The updated UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Updates a user data",
            description = "Updates information about an existing User"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or User not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, Authentication authentication) {
        try {

            //TODO: Capire questa funzione? user.getID? E L'autentificazione?
            UserDTO updatedUser = userService.updateUser(user.getId(), user);
            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates a Review data in the system.
     *
     * @param parameters The new ReviewParameterDTO containing the updated review data.
     * @param authentication Security context of the user performing the action.
     * @return The updated ReviewParameterDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Updates a Review",
            description = "Updates data of an already existing event's review."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewParameterDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, User or Event are not found, or the User's review of the Event is not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PutMapping("review")
    public ResponseEntity<?> updateReview(@RequestBody ReviewParameterDTO parameters,
                                          Authentication authentication) {
        try {
            ReviewParameterDTO updatedReview = userService.updateReview(parameters.getEventId(), parameters, authentication.getName());
            return ResponseEntity.ok(updatedReview);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Removes a Review from the system.
     *
     * @param eventID The id of the Event the Review is related to.
     * @param authentication Security context of the user performing the action.
     * @return The updated UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Removes a review",
            description = "Removes a user review from the event's reviews."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Review removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, User or Event are not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @DeleteMapping("review/{eventID}")
    public ResponseEntity<?> deleteReview(@PathVariable String eventID, Authentication authentication) {
        try{
            UserDTO userDTO = userService.deleteReview(eventID, authentication.getName());
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Removes an Attendance from the system.
     *
     * @param eventID The id of the Event the Attendance is related to.
     * @param authentication Security context of the user performing the action.
     * @return The updated UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Removes a user attendance to an event",
            description = "Removes the previously stated attendance of a user to a specific event."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Attendance removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, User or Event are not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @DeleteMapping("attending/{eventID}")
    public ResponseEntity<?> deleteAttendance(@PathVariable String eventID, Authentication authentication) {
        try{

            UserDTO userDTO = userService.deleteAttendance(eventID, authentication.getName());
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Removes a friendship from the system.
     *
     * @param friendUsername The id of the Event the Review is related to.
     * @param authentication Security context of the user performing the action.
     * @return The updated UserDTO if successful, or an error message if the request is invalid.
     */
    @Operation(
            summary = "Removes a friendship",
            description = "Removes a friendship between two users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friendship removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, authenticated user or friend user are not found, or the friendship is not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @DeleteMapping("/friendship/{friendUsername}")
    public ResponseEntity<?> deleteFriendship(@PathVariable String friendUsername, Authentication authentication) {
        try{
            UserDTO userDTO = userService.deleteFriendship(authentication.getName(), friendUsername);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
