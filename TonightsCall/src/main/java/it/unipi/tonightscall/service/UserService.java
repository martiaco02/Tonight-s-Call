package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.UserDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.graph.EventNode;
import it.unipi.tonightscall.entity.graph.ReviewRelationship;
import it.unipi.tonightscall.entity.graph.UserNode;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import it.unipi.tonightscall.repository.graph.UserGraphRepository;
import it.unipi.tonightscall.utilies.Mapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

    public final UserRepository userRepository;
    public final EventRepository eventRepository;

    public final UserGraphRepository userGraphRepository;
    public final EventGraphRepository eventGraphRepository;

    public UserService(UserRepository userRepository, EventRepository eventRepository, UserGraphRepository userGraphRepository, EventGraphRepository eventGraphRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userGraphRepository = userGraphRepository;
        this.eventGraphRepository = eventGraphRepository;
    }

    public UserDTO addFriendship(String usernameNewFriend, String myName) {

        User me = userRepository.findByUsername(myName)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        User friend =  userRepository.findByUsername(usernameNewFriend)
                .orElseThrow(() -> new RuntimeException("Friend User not found!"));

        if (me.getFriends().contains(usernameNewFriend)) {
            throw new RuntimeException("User is already friend!");
        }

        me.getFriends().add(usernameNewFriend);
        friend.getFriends().add(myName);

        userRepository.save(me);
        userRepository.save(friend);

        UserNode myNode = userGraphRepository.findById(me.getId())
                .orElseThrow(() -> new RuntimeException("User Node not found!"));

        UserNode friendNode = userGraphRepository.findById(friend.getId())
                .orElseThrow(() -> new RuntimeException("Friend Node not found!"));

        myNode.getFriends().add(friendNode);

        userGraphRepository.save(myNode);
        userGraphRepository.save(friendNode);

        return Mapper.mapUserToDto(me);
    }

    public EventDTO addAttendance(String eventID, String ticket_type, String name) {

        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        User me = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        event.getAttendees().add(new Attendent(
                me.getId(),
                ticket_type,
                me.getEmail(),
                me.getUsername(),
                me.getAddress().getCityName(),
                me.getDateOfBirth()
        ));

        eventRepository.save(event);

        EventNode eventNode = eventGraphRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event Node not found!"));

        UserNode myNode = userGraphRepository.findById(me.getId())
                .orElseThrow(() -> new RuntimeException("User Node not found!"));

        myNode.getAttendees().add(eventNode);
        userGraphRepository.save(myNode);

        return Mapper.mapEventToDTO(event);
    }

    public EventDTO addReview(String eventID, String text, int score, String name) {

        User me = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        for (ReviewEvent r : me.getReviewedEvents()) {
            if (r.getEventName().equals(event.getEventName())) {
                throw new RuntimeException("The user already give a review to this event!");
            }
        }

        me.getReviewedEvents().add(new ReviewEvent(event.getEventName(), score));
        event.getReviews().add(new Review(score, me.getUsername(), text));

        double oldScore = event.getTotalReview();
        int oldTotal = event.getTotalReview();
        event.setEventScore((oldScore*oldTotal + score)/(oldTotal + 1));

        event.setTotalReview(oldTotal + 1);

        userRepository.save(me);
        eventRepository.save(event);

        UserNode myNode = userGraphRepository.findById(me.getId())
                .orElseThrow(() -> new RuntimeException("User Node not found!"));

        EventNode eventNode = eventGraphRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event Node not found!"));

        ReviewRelationship rr = new ReviewRelationship();
        rr.setEvent(eventNode);
        rr.setScore(score);
        if (myNode.getReviews() == null) {
            myNode.setReviews(new ArrayList<>());
        }
        myNode.getReviews().add(rr);

        userGraphRepository.save(myNode);

        return Mapper.mapEventToDTO(event);
    }
}
