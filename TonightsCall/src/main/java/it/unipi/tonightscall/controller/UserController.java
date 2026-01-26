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

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/RegisterFriend")
    public ResponseEntity<?> registerFriend(@RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            UserDTO updatedUser = userService.addFriendship(payload.get("username"), authentication.getName());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/attending")
    public ResponseEntity<?> attending(@RequestBody Map<String, String> payload, Authentication authentication) {
        try{
            EventDTO updatedEvent = userService.addAttendance(payload.get("event_id"), payload.get("ticket_type"), authentication.getName());
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/review")
    public ResponseEntity<?> review(@RequestBody ReviewParameterDTO parameters, Authentication authentication) {
        try {
            EventDTO updatedEvent = userService.addReview(parameters.getEventId(), parameters.getText(), parameters.getScore(), authentication.getName());
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,
                                        @RequestBody UserDTO user,
                                        Authentication authentication) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!existingUser.getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Forbidden: You can only update your own profile.");
            }

            UserDTO updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("review")
    public ResponseEntity<?> updateReview(@RequestBody ReviewParameterDTO parameters,
                                          Authentication authentication) {
        try {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setScore(parameters.getScore());
            reviewDTO.setText(parameters.getText());
            reviewDTO.setUsername(authentication.getName());
            ReviewDTO updatedReview = userService.updateReview(parameters.getEventId(), reviewDTO);
            return ResponseEntity.ok(updatedReview);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("review/{eventID}")
    public ResponseEntity<?> deleteReview(@PathVariable String eventID, String userID, Authentication authentication) {
        try{
            User user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if(!user.getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Forbidden: You can only delete your own reviews.");
            }

            userService.deleteReview(eventID, userID);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("attendance/{eventID}")
    public ResponseEntity<?> deleteAttendance(@PathVariable String eventID, String userID, Authentication authentication) {
        try{
            User user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if(!user.getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Forbidden: You can only delete your own reviews.");
            }

            userService.deleteAttendance(eventID, userID);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{userID}/friendship/{friendID}")
    public ResponseEntity<?> deleteFriendship(@PathVariable String userID, @PathVariable String friendID, Authentication authentication) {
        try{
            User user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if(!user.getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Forbidden: You can only delete your own friendships.");
            }

            userService.deleteFriendship(userID, friendID);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
