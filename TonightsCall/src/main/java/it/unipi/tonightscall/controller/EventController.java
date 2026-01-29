package it.unipi.tonightscall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.StatisticsDTO;
import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.service.EventService;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.geo.Point;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller handling operations specific to Event.
 */
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
     * @return ResponseEntity containing the page or error
     */
    @Operation(
            summary = "Retrieve all events",
            description = "Fetches a paginated list of all available events. The page size is configured in the service."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No events found (empty page)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or page number",
                    content = @Content
            )
    })
    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents(@RequestParam(defaultValue = "0") @Min(0) int page) {
        try {
            Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
            Page<@NonNull EventDTO> events = this.eventService.getAllEvents(pageable);

            if (events.isEmpty())
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Find an event with a certain id
     *
     * @param id  The id of the event
     * @return ResponseEntity containing the EventDTOs or error
     */
    @Operation(
            summary = "Get event by ID",
            description = "Returns the details of a specific event looking up by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        try {
            EventDTO event = this.eventService.getEventById(id);
            if  (event == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Find events based on their topics
     *
     * @param topics  The topics of the event
     * @param page  The number of pages to return (the result is paginated)
     * @return ResponseEntity containing the page or error
     */
    @Operation(
            summary = "Search events by topics",
            description = "Retrieves a paginated list of events that match one or more of the provided topics (categories)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Events found matching the topics",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No events found for these topics",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content
            )
    })
    @GetMapping("/search/topics")
    public ResponseEntity<?> getEventsByTopic( @RequestParam List<String> topics, @RequestParam(defaultValue = "0") @Min(0) int page ) {
        try {
            Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
            Page<@NonNull EventDTO> eventsPage = this.eventService.getEventsByTopic(topics, pageable);

            if (eventsPage == null) {
                return ResponseEntity.notFound().build();
            } else if (eventsPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(eventsPage);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Find events based on their starting date
     *
     * @param startingDate  The starting date of the event
     * @param page  The number of pages to return (the result is paginated)
     * @return ResponseEntity containing the page or error
    */
     @Operation(
         summary = "Search events by date",
         description = "Retrieves a paginated list of events starting exactly on the specified date."
     )
     @ApiResponses(value = {
         @ApiResponse(
             responseCode = "200",
             description = "Events found for the given date",
             content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
         ),
         @ApiResponse(
             responseCode = "204",
             description = "No events found for this date",
             content = @Content
         ),
         @ApiResponse(
             responseCode = "400",
             description = "Invalid date format or request parameters",
             content = @Content
         )
     })
    @GetMapping("/search/date")
    public ResponseEntity<?> getEventsByDate(@RequestParam LocalDate startingDate, @RequestParam(defaultValue = "0") @Min(0) int page) {
         try {
             Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
             Page<@NonNull EventDTO> eventsPage = this.eventService.getEventsByDate(startingDate, pageable);

             if (eventsPage == null || eventsPage.isEmpty()) {
                 return ResponseEntity.noContent().build();
             }

             return ResponseEntity.ok(eventsPage);

         } catch (Exception e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }

    /**
     * Find events in a specific city
     *
     * @param cityName The name of the specific city in which to look for available events
     * @param page The number of pages to return
     * @return ResponseEntity containing the page or error
     */

    @GetMapping("/city/{cityName}")
    public ResponseEntity<?> getEventsByCity(@PathVariable String cityName, @RequestParam (defaultValue = "0") @Min(0) int page) {
        try{
            Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
            Page<@NonNull EventDTO> eventsPage = this.eventService.getEventsByCity(cityName, pageable);
            if (eventsPage == null || eventsPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(eventsPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Find events based on their location
     *
     * @param longitude The longitude of the center of the circle
     * @param latitude The latitudeof the center of the circle
     * @param distance  The maximum admissible distance from "location"
     * @param page  The number of pages to return (the result is paginated)
     * @return ResponseEntity containing the page or error
     */
    @Operation(
            summary = "Search events by location",
            description = "Finds events within a specific radius (in Km) from a given geographic coordinate."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events found in the area",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "204", description = "No events found nearby", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates", content = @Content)
    })
    @GetMapping("/search/location")
    public ResponseEntity<?> getEventsByLocation(@RequestParam double longitude,@RequestParam double latitude, Distance distance, @RequestParam(defaultValue = "0") @Min(0) int page) {

        try {
            Point location = new Point(longitude, latitude);
            Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);

            Page<@NonNull EventDTO> events = this.eventService.getEventsByLocation(location, distance, pageable);
            if (events == null) {
                return ResponseEntity.notFound().build();
            } else if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Find events based on their topics and starting date
     *
     * @param categories  The topics of the events
     * @param date  The starting date of the events
     * @param page  The number of pages to return (the result is paginated)
     * @return ResponseEntity containing the page or error
     */
    @Operation(
            summary = "Search events by topic and date",
            description = "Retrieves events that match at least one of the provided categories AND start on the specified date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching events found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "204", description = "No events found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content)
    })
    @GetMapping("/search/topic-date")
    public ResponseEntity<?> getEventsByTopicAndDate(@RequestParam List<String> categories, @RequestParam LocalDate date, @RequestParam(defaultValue = "0") @Min(0) int page) {
        try {
            Pageable pageable = PageRequest.of(page, this.eventService.PAGE_SIZE);
            Page<@NonNull EventDTO> events = this.eventService.getEventsByTopicAndDate(categories, date, pageable);

            if (events == null) {
                return ResponseEntity.notFound().build();
            } else if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event Not Found",
                    content = @Content
            )
    })
    @PutMapping("/update-event/{event_id}")
    public ResponseEntity<?> updateEvent(@PathVariable String event_id, Authentication authentication, @RequestBody EventDTO eventDTO) {
        try{
            EventDTO updatedEvent = eventService.updateEvent(event_id, authentication.getName(), eventDTO);
            if (updatedEvent == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>("403 - Forbidden: You don't have permission", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Calculates the statistics of an event.
     *
     * @param event_id  The ID of the event.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The statisticsDTO containing the results of the calculation.
     */
    @Operation(
            summary = "Calculates and event's Statistics",
            description = "Calculates Statistics relative to a specific event and updated its related data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics calculated and event updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatisticsDTO.class))
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event Not Found",
                    content = @Content
            )
    })
    @GetMapping("statistics/{event_id}")
    public ResponseEntity<?> calculateStatistics(@PathVariable String event_id, Authentication authentication) {
        try{
            StatisticsDTO statisticsDTO = eventService.calculateStatistics(event_id, authentication.getName());
            return ResponseEntity.ok(statisticsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Publishes the statistics of an event.
     *
     * @param event_id  The ID of the event
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The eventDTO containing the statistics.
     */
    @Operation(
            summary = "Updates an Event's data",
            description = "Updates information about an existing Event."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event's statistics retrieved succesfully",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event Not Found",
                    content = @Content
            )
    })
    @GetMapping("/publishStatistics/{event_id}")
    public ResponseEntity<?> publishStatistics(@PathVariable String event_id, Authentication authentication) {
        try{
            EventDTO eventDTO = eventService.publishStatistics(event_id, authentication.getName());
            return  ResponseEntity.ok(eventDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}