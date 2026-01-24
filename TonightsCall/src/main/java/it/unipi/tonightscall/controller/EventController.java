package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.service.EventService;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
