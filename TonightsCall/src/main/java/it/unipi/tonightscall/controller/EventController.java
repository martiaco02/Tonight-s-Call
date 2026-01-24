package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Event> getAllEvents() { return this.eventService.getAllEvents(); }

    @GetMapping("/{id}")
    public Optional<Event> getEventById(@PathVariable String id) { return this.eventService.getEventById(id); }
}
