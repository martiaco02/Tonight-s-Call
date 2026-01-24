package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.service.EventService;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public Page<@NonNull Event> getAllEvents(@RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.eventService.getAllEvents(pageable);
    }

    @GetMapping("/{id}")
    public Optional<Event> getEventById(@PathVariable String id) { return this.eventService.getEventById(id); }

    //  --- Find events by topic ---
    //  Find events that contain at least one of the provided categories
    @GetMapping
    public Page<@NonNull Event> getEventsByTopic(Collection<List<String>> topics, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.eventService.getEventsByTopic(topics, pageable);
    }

    //  Find events that contain every provided category
    @GetMapping
    public Page<@NonNull Event> getEventsByAllTopics(Collection<List<String>> topics, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.eventService.getEventsByAllTopics(topics, pageable);
    }

    //  --- Find events by starting date ---
    //  Find events that start on the provided date
    @GetMapping
    public Page<@NonNull Event> getEventsByDate(LocalDate startingDate, @RequestParam(defaultValue = "0") @Min(0) int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.eventService.getEventsByDate(startingDate, pageable);
    }
}
