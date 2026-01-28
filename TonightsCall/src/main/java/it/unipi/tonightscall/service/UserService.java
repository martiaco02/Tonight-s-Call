package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.*;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.entity.graph.EventNode;
import it.unipi.tonightscall.entity.graph.ReviewRelationship;
import it.unipi.tonightscall.entity.graph.TopicNode;
import it.unipi.tonightscall.entity.graph.UserNode;
import it.unipi.tonightscall.repository.document.EventRepository;
import it.unipi.tonightscall.repository.document.UserRepository;
import it.unipi.tonightscall.repository.graph.EventGraphRepository;
import it.unipi.tonightscall.repository.graph.TopicGraphRepository;
import it.unipi.tonightscall.repository.graph.UserGraphRepository;
import it.unipi.tonightscall.utilies.Mapper;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class UserService {

    public final UserRepository userRepository;
    public final EventRepository eventRepository;

    public final UserGraphRepository userGraphRepository;
    public final EventGraphRepository eventGraphRepository;
    private final PasswordEncoder passwordEncoder;
    private final TopicGraphRepository topicGraphRepository;

    public final int PAGE_SIZE = 10;

    public UserService(UserRepository userRepository, EventRepository eventRepository, UserGraphRepository userGraphRepository, EventGraphRepository eventGraphRepository, PasswordEncoder passwordEncoder, TopicGraphRepository topicGraphRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userGraphRepository = userGraphRepository;
        this.eventGraphRepository = eventGraphRepository;
        this.passwordEncoder = passwordEncoder;
        this.topicGraphRepository = topicGraphRepository;
    }

    /**
     * Adds a new friendship between two Users.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the two users (current user and future friend) exist.</li>
     *      <li>Adds the friend to the current user friend list.</li>
     *      <li>Adds the user to the friend's friend list.</li>
     *      <li>Saves the users to MongoDB.</li>
     *      <li>Saves the corresponding nodes in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param usernameNewFriend The Username of the user who is being befriended.
     * @param myID The id of the User who is making the friendship.
     * @return The new UserDTO.
     * @throws RuntimeException If one of the two users is not found.
     */
    public UserDTO addFriendship(String usernameNewFriend, String myID) {

        User me = userRepository.findById(myID)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        User friend =  userRepository.findByUsername(usernameNewFriend)
                .orElseThrow(() -> new RuntimeException("Friend User not found!"));

        if (me.getFriends().contains(usernameNewFriend)) {
            throw new RuntimeException("User is already friend!");
        }

        me.getFriends().add(usernameNewFriend);
        friend.getFriends().add(me.getUsername());

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

    /**
     * Returns all the users
     *
     * @param pageable for implementing the pagination
     * @return Page of UserDTO or null
     */
    public Page<@NonNull UserDTO> getAllUsers(Pageable pageable) {

        Page<@NonNull User> users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            return null;
        }

        return users.map(Mapper::mapUserToDto);
    }

    /**
     * Returns the User from the id
     *
     * @param id The id of the user
     * @return The wanted user or null
     */
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user == null ? null : Mapper.mapUserToDto(user);
    }


    /**
     * Adds a User new Attendance to an Event.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the User exists.</li>
     *      <li>Checks if the Event exists.</li>
     *      <li>Adds the User's attendance to the Event's list of attendants.</li>
     *      <li>Adds the Event to the list of events the User is going to attend.</li>
     *      <li>Saves the User and Event to MongoDB.</li>
     *      <li>Saves the corresponding nodes in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param eventID The id of the event the Users wants to attend.
     * @param ticket_type The ticket type the User selected.
     * @param id The id of the User.
     * @return The new EventDTO.
     * @throws RuntimeException If the User or the Event are not found (both in MongoDB or Neo4j).
     */
    public EventDTO addAttendance(String eventID, String ticket_type, String id) {

        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        User me = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        boolean flag = false;
        if (
                (event.getTicketPrice() == null || event.getTicketPrice().isEmpty())
                && (ticket_type == null || ticket_type.isEmpty())
        ) {
            flag = true;
        } else {
            if (event.getTicketPrice() != null) {
                for (String key : event.getTicketPrice().keySet()) {
                    if (key.equals(ticket_type)) {
                        flag = true;
                        break;
                    }
                }
            }
        }

        if (!flag) {
            throw new RuntimeException("Ticket type Invalid!");
        }

        if (event.getAttendees() != null && !event.getAttendees().isEmpty()) {
            for (Attendent attendent : event.getAttendees()) {
                if (attendent.getUsername().equals(me.getUsername())) {
                    throw new RuntimeException("Username already attending the event!");
                }
            }
        }

        if (event.getAttendees() == null) {
            event.setAttendees(new ArrayList<>());
        }
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

    /**
     * Adds a Review to an Event.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the User exists.</li>
     *      <li>Checks if the Event exists.</li>
     *      <li>Adds the Event to the User's list of ReviewdEvents.</li>
     *      <li>Adds the Review to the Event's list of reviews.</li>
     *      <li>Saves the User and Event to MongoDB.</li>
     *      <li>Saves the corresponding nodes in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param eventID The id of the reviewed Event.
     * @param text The text of the Review.
     * @param score The score of the Review.
     * @param id The id of the User who left a Review.
     * @return The new EventDTO.
     * @throws RuntimeException If the User or the Event are not found (both in MongoDB or Neo4j).
     */
    public EventDTO addReview(String eventID, String text, int score, String id) {

        User me = userRepository.findById(id)
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

    /**
     * Updates a User data.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the specific user exists</li>
     *      <li>Checks for consistency: username and date of birth can't change, whereas friendships and reviewed events can't change directly in this function</li>
     *      <li>Updates the data.</li>
     *      <li>Saves the User to MongoDB.</li>
     *      <li>Updates the corresponding node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param userID The ID of the user to update.
     * @param newUserDTO The UserDTO containing the updated data.
     * @return The updated UserDTO.
     * @throws RuntimeException If the User is not found or if the user tries to update username, date of birth, friendships or reviewed events.
     */
    @Transactional
    public UserDTO updateUser(String userID, UserDTO newUserDTO){
        User oldUser =  userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(newUserDTO.getId() != null && !oldUser.getId().equals(newUserDTO.getId())){
            throw new RuntimeException("You can only update your own profile!");
        }
        // checking consistency: username can't change
        if(newUserDTO.getUsername() != null && !newUserDTO.getUsername().equals(oldUser.getUsername())){
            throw new RuntimeException("Can't update username!");
        }

        // checking consistency: date_of_birth can't change
        if(newUserDTO.getDateOfBirth() != null && !newUserDTO.getDateOfBirth().isEqual(oldUser.getDateOfBirth())){
            throw new RuntimeException("Can't update date of birth!");
        }

        // checking consistency: friends and reviews can't be directly changed
        if(newUserDTO.getReviewedEvents() != null && !newUserDTO.getReviewedEvents().isEmpty()){
            throw new RuntimeException("Can't update reviewed events!");
        }

        if (newUserDTO.getFriends() != null && !newUserDTO.getFriends().isEmpty()){
            throw new RuntimeException("Can't update friends!");
        }

        boolean update_graph = false;

        // updating the inserted data
        if(newUserDTO.getName() != null) {oldUser.setName(newUserDTO.getName());}
        if(newUserDTO.getLastname() != null) {oldUser.setLastname(newUserDTO.getLastname());}
        if(newUserDTO.getEmail() != null) {oldUser.setEmail(newUserDTO.getEmail()); update_graph = true;}
        if(newUserDTO.getPassword() != null) {oldUser.setPassword(passwordEncoder.encode(newUserDTO.getPassword()));}
        if(newUserDTO.getInterests() != null) {
            update_graph = true;
            //oldUser.setInterests(newUserDTO.getInterests());
            for (String interest : newUserDTO.getInterests()) {
                if (!oldUser.getInterests().contains(interest)) {
                    oldUser.getInterests().add(interest);
                }
            }
        }

        if(newUserDTO.getHomeTown() != null) {
            // updating all of the address data
            Address newAddress = new Address();

            newAddress.setFullAddress(newUserDTO.getHomeTown().getFullAddress());
            newAddress.setCityName(newUserDTO.getHomeTown().getCityName());

            // we have to update the location (coordinates)
            Location newLocation = new Location();
            newLocation.setType(newUserDTO.getHomeTown().getLoc().getType());
            newLocation.setCoordinates(newUserDTO.getHomeTown().getLoc().getCoordinates());
            newAddress.setLocation(newLocation);

            oldUser.setAddress(newAddress);
            // in this case we also have to update the graph (coordinates)
            update_graph = true;
        }

        // updating MongoDB document
        userRepository.save(oldUser);

        System.out.println(update_graph);
        // updating graphDB if necessary
        if(update_graph){
            UserNode myNode = userGraphRepository.findById(oldUser.getId())
                    .orElseThrow(() -> new RuntimeException("User Node not found!"));

            if (oldUser.getAddress() != null && oldUser.getAddress().getLocation() != null) {
                myNode.setCoordinates(oldUser.getAddress().getLocation().getCoordinates());
            }

            if (newUserDTO.getEmail() != null) {
                myNode.setEmail(newUserDTO.getEmail());
            }

            // 2. Update Interests (if changed)
            if (newUserDTO.getInterests() != null) {
                // Clear existing relationships first
                myNode.getInterests().clear();

                // Loop through the new list of strings using index 'i'
                for (int i = 0; i < newUserDTO.getInterests().size(); i++) {
                    String topicName = newUserDTO.getInterests().get(i);

                    // Find the topic node in the DB by its name
                    // (Ensure you have injected 'topicGraphRepository' in your Service!)
                    TopicNode topicNode = topicGraphRepository.findById(topicName)
                            .orElse(new  TopicNode(topicName.toUpperCase()));

                    myNode.getInterests().add(topicNode);
                }
            }

            System.out.println(myNode);
            userGraphRepository.save(myNode);
        }

        return Mapper.mapUserToDto(oldUser);

    }

    /**
     * Updates a Review.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the specific user exists.</li>
     *      <li>Checks if the event exists.</li>
     *      <li>Updates the review for the user.</li>
     *      <li>Updates the review and score for the event.</li>
     *      <li>Saves the User to MongoDB.</li>
     *      <li>Saves the Event to MongoDB.</li>
     *      <li>Updates the corresponding node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param EventID The ID of the event the review was written for.
     * @param newReviewDTO The ReviewDTO containing the updated data.
     * @param id The id of the user who is making the update of their review.
     * @return The updated ReviewDTO.
     * @throws RuntimeException If the User is not found, if the Event is not found or if the user didn't actually review the event.
     */
    @Transactional
    public ReviewParameterDTO updateReview(String EventID, ReviewParameterDTO newReviewDTO, String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Event event = eventRepository.findById(EventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        boolean found = false;
        // checks if the user actually reviewed the event
        for(int i=0; i<event.getReviews().size(); i++){
            if(event.getReviews().get(i).getUsername().equals(user.getUsername())){
                found = true;
                break;
            }
        }
        if(!found){
            throw new RuntimeException("You didn't review this event!");
        }


        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setScore(newReviewDTO.getScore());
        reviewDTO.setText(newReviewDTO.getText());
        reviewDTO.setUsername(user.getUsername());

        // we have to update both the specific user's reviews and the specific event's reviews

        // updating the user's reviews: looking for the specific event's review in the list
        // of the reviews the specific user left
        if (user.getReviewedEvents() != null) {
            for(int i=0; i<user.getReviewedEvents().size(); i++){
                ReviewEvent re =  user.getReviewedEvents().get(i);
                if(re.getEventName().equals(event.getEventName())){
                    re.setScore(newReviewDTO.getScore());
                    break;
                }
            }
        }

        // updating the event's reviews: looking for the review left by the specific user in
        // the list
        int oldScore = 0;
        boolean foundReview = false;
        if(event.getReviews() != null) {
            for(int i=0; i<event.getReviews().size(); i++){
                Review re =  event.getReviews().get(i);
                if(re.getUsername().equals(user.getUsername())){
                    oldScore = re.getScore();
                    re.setScore(newReviewDTO.getScore());
                    re.setText(newReviewDTO.getText());
                    foundReview = true;
                    break;
                }
            }
        }

        if(!foundReview){
            throw new RuntimeException("Review not found!");
        }

        // updating the event total score
        double newScore = (event.getEventScore()*event.getTotalReview()-oldScore+ newReviewDTO.getScore())/event.getTotalReview();
        event.setEventScore(newScore);

        // saving document
        userRepository.save(user);
        eventRepository.save(event);

        // updating graphs if needed: only the user's node and the review node need to be updated
        UserNode userNode =  userGraphRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(userNode.getReviews() != null) {
            for(int i=0; i<userNode.getReviews().size(); i++){
                ReviewRelationship rr = userNode.getReviews().get(i);
                if(rr.getEvent().getId().equals(EventID)){
                    rr.setScore(newReviewDTO.getScore());
                    break;
                }
            }
        }
        userGraphRepository.save(userNode);

        return newReviewDTO;
    }

    /**
     * Deletes a Review.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the specific user exists.</li>
     *      <li>Checks if the specific event exists.</li>
     *      <li>Deletes the review from the user.</li>
     *      <li>Deletes the review from the event.</li>
     *      <li>Saves changes to MongoDB</li>
     *      <li>Saves changes in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param eventID The ID of the event the Review belongs to.
     * @param id The Username of the user the Review was written by.
     * @return The updated UserDTO
     * @throws RuntimeException If the User is not found or if the event is not found.
     */
    @Transactional
    public UserDTO deleteReview(String eventID, String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        UserNode userNode = userGraphRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // deleting the review from the user
        for(int i=0; i<user.getReviewedEvents().size(); i++){
            ReviewEvent re =  user.getReviewedEvents().get(i);
            if(re.getEventName().equals(event.getEventName())){
                user.getReviewedEvents().remove(i);
                break;
            }
        }

        int left_score = 0;
        // deleting the review from the event
        for(int i=0; i<event.getReviews().size(); i++){
            Review re =  event.getReviews().get(i);
            if(re.getUsername().equals(user.getUsername())){
                left_score = re.getScore();
                event.getReviews().remove(i);
                // updating the event score
                double total_score = event.getEventScore()*event.getTotalReview() - left_score;
                int reviews_number = event.getTotalReview() - 1;

                total_score = total_score / reviews_number;
                event.setEventScore(total_score);
                event.setTotalReview(reviews_number);
            }
        }

        // updating also user graph
        for(int i=0; i<userNode.getReviews().size(); i++){
            if(userNode.getReviews().get(i).getEvent().getId().equals(event.getId())){
                userNode.getReviews().remove(i);
                userGraphRepository.save(userNode);
                break;
            }
        }

        userRepository.save(user);
        eventRepository.save(event);

        return Mapper.mapUserToDto(user);
    }

    /**
     * Deletes an Attendance to an Event.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the specific user exists</li>
     *      <li>Checks if the specific event exists</li>
     *      <li>Deletes the attendance</li>
     *      <li>Saves the changes to MongoDB.</li>
     *      <li>Saves the changes to the User Node in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param eventID The ID of the event the user was going to attend.
     * @param id The id of the user whose attendee must be deleted.
     * @return The updated UserDTO
     * @throws RuntimeException If the User is not found or if the event is not found.
     */
    @Transactional
    public UserDTO deleteAttendance(String eventID, String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        for(int i=0; i<event.getAttendees().size(); i++){
            if(event.getAttendees().get(i).getUsername().equals(user.getUsername())){
                event.getAttendees().remove(i);
                break;
            }
        }

        userGraphRepository.deleteAttendecy(id, eventID);

        eventRepository.save(event);
        userRepository.save(user);

        return Mapper.mapUserToDto(user);
    }

    /**
     * Deletes a friendship between two users.
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the specific user exists</li>
     *      <li>Checks if the specific user's friend exists</li>
     *      <li>Checks if the users are actually friends (user's friend list and friend's friend list)</li>
     *      <li>Saves the changes to MongoDB.</li>
     *      <li>Saves the changes to the User Nodes in the Graph Database (Neo4j).</li>
     * </ol>
     * </p>
     *
     * @param id The id of the User who wants to unfriend another User.
     * @param friendUsername the Username of the User that has to be unfriended.
     * @return The updated UserDTO
     * @throws RuntimeException If one of the two Users is not found or if the friendship is not found.
     */
    @Transactional
    public UserDTO deleteFriendship(String id, String friendUsername){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("User friend not found!"));

        if (user.getFriends() == null)
            throw new RuntimeException("User friends not found!");

        if(!user.getFriends().contains(friend.getUsername()) || !friend.getFriends().contains(user.getUsername())){
            throw new RuntimeException("You are not friend with this user!");
        }
        // checking user's friend list to find friendship with friend and remove it
        if(user.getFriends().contains(friend.getUsername())){
            user.getFriends().remove(friend.getUsername());
        }

        // checking friend's friend list to find friendship with user and remove it
        if(friend.getFriends() != null && friend.getFriends().contains(user.getUsername())){
            friend.getFriends().remove(user.getUsername());
        }

        userRepository.save(user);
        userRepository.save(friend);

        userGraphRepository.deleteFriendship(user.getUsername(), friend.getUsername());

        return Mapper.mapUserToDto(user);
    }
}
