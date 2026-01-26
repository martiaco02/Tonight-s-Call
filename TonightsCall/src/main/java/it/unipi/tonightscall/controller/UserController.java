package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.ReviewDTO;
import it.unipi.tonightscall.DTO.ReviewParameterDTO;
import it.unipi.tonightscall.DTO.UserDTO;
import it.unipi.tonightscall.entity.document.User;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @PostMapping("/RegisterFriend")
    public ResponseEntity<?> registerFriend(@RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            UserDTO updatedUser = userService.addFriendship(payload.get("username"), authentication.getName());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registers a new attendance in the system.
     *
     * @param payload The user information transfer object containing registration details.
     * @param authentication Security context of the user performing the action.
     * @return The updated EventDTO if successful, or an error message if the request is invalid.
     */
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

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, Authentication authentication) {
        try {

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
