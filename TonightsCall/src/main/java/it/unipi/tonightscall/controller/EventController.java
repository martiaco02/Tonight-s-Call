package it.unipi.tonightscall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.service.EventService;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Find every event
     *
     * @param page  The number of pages to return (the result is paginated)
     */
    @GetMapping
    public Page<@NonNull Event> getAllEvents(@RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
        return this.eventService.getAllEvents(pageable);
    }

    /**
     * Find an event with a certain id
     *
     * @param id  The id of the event
     */
    @GetMapping("/{id}")
    public Optional<Event> getEventById(@PathVariable String id) { return this.eventService.getEventById(id); }

    /**
     * Find events based on their topics
     *
     * @param topics  The topics of the event
     * @param page  The number of pages to return (the result is paginated)
     */
    @GetMapping("/topic")
    public Page<@NonNull Event> getEventsByTopic(@RequestParam List<String> topics, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
        return this.eventService.getEventsByTopic(topics, pageable);
    }

    /**
     * Find events based on their starting date
     *
     * @param startingDate  The starting date of the event
     * @param page  The number of pages to return (the result is paginated)
     */
    @GetMapping("/date")
    public Page<@NonNull Event> getEventsByDate(@RequestParam LocalDate startingDate, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
        return this.eventService.getEventsByDate(startingDate, pageable);
    }

    /**
     * Find events based on their location
     *
     * @param location  The center point around which to look for
     * @param distance  The maximum admissible distance from "location"
     * @param page  The number of pages to return (the result is paginated)
     */
    @GetMapping("/location")
    public Page<@NonNull Event> getEventsByLocation(@RequestParam Point location, Distance distance, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
        return this.eventService.getEventsByLocation(location, distance, pageable);
    }

    /**
     * Find events based on their topics and starting date
     *
     * @param categories  The topics of the events
     * @param date  The starting date of the events
     * @param page  The number of pages to return (the result is paginated)
     */
    @GetMapping("/topic-date")
    public Page<@NonNull Event> getEventsByTopicAndDate(@RequestParam List<String> categories, @RequestParam LocalDate date, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
        return this.eventService.getEventsByTopicAndDate(categories, date, pageable);
    }

    /**
     * Updates the data of an event
     *
     * @param event_id  The ID of the updated event
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @param eventDTO The updated details of the event
     * @return The updated eventDTO and the list of emails of every user which stated their presence at the event if successful, or an error message otherwise.
     */
    @Operation(
            summary = "Updates an Event's data",
            description = "Updates information about an existing Event."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, Event not found",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PutMapping("/update-event/{event_id}")
    public ResponseEntity<?> updateEvent(@PathVariable String event_id, Authentication authentication, @RequestBody EventDTO eventDTO) {
        try{
            EventDTO updatedEvent = eventService.updateEvent(event_id, authentication.getName(), eventDTO);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}