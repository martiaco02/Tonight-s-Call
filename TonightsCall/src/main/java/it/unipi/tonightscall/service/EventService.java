package it.unipi.tonightscall.service;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    public final EventRepository eventRepository;
    public final EventGraphRepository eventGraphRepository;

    EventService(EventRepository eventRepository, EventGraphRepository eventGraphRepository) {
        this.eventRepository = eventRepository;
        this.eventGraphRepository = eventGraphRepository;
    }

    public List<Event> getAllEvents() { return eventRepository.findAll(); }

    public Optional<Event> getEventById(String id) { return eventRepository.findById(id); }
}
