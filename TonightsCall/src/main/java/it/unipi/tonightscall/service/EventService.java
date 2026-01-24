package it.unipi.tonightscall.service;

import it.unipi.tonightscall.entity.document.Event;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public Page<@NonNull Event> getAllEvents(Pageable pageable) { return this.eventRepository.findAll(pageable); }

    public Optional<Event> getEventById(String id) { return this.eventRepository.findById(id); }

    //  Find events that contain at least one of the provided categories
    public Page<@NonNull Event> getEventsByTopic(Collection<List<String>> topics, Pageable pageable) { return this.eventRepository.findByCategoriesIn(topics, pageable); }

    //  Find events that contain every provided category
    public Page<@NonNull Event> getEventsByAllTopics(Collection<List<String>> topics, Pageable pageable) { return this.eventRepository.findByCategoriesAll(topics, pageable); }
}
