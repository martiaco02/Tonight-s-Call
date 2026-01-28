package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.EventNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventGraphRepository extends Neo4jRepository<@NonNull EventNode,@NonNull String> {

    /**
     * Delete all  :IS_ABOUT relationship outgoing from an Event
     *
     * @param eventId The id of the event
     */
    @Query("MATCH (e:Event {id: $eventId})-[r:`:IS_ABOUT`]->() DELETE r")
    void deleteAllIsAboutRelationships(String eventId);

    /**
     * Obtain all the email of the users that attended one event
     *
     * @param eventId The id of the event
     * @return List<String> The list containing the email
     */
    @Query("MATCH (e:Event {id : $eventId})<-[:`:ATTENDS`]-(u:User) RETURN u.email;")
    List<String> findAllEmailFromAttendingUser(String eventId);

    /**
     * Performs a graph traversal to find relevant events.
     * The suggestion algorithm considers:
     * <ol>
     *  <li> Events organized by organizers the user has previously rated highly (>= 4). </li>
     *  <li> Events associated with topics the user likes. </li>
     *  <li> Events highly rated by the user's friends.</li>
     * </ol>
     * Filters out events the user has already attended or reviewed, events with
     * poor ratings from friends, and applies geospatial filtering using the Haversine formula.
     *
     * @param userId       ID of the user receiving suggestions.
     * @param distanceKm   Geographic radius limit.
     * @param startingDate Minimum starting date for the events.
     * @param limit        Pagination limit.
     * @return A list of {@link EventNode} entities matching the criteria.
     */
    @Query("MATCH (user:User {id: $userId})-[review:`:REVIEWS`]->(event:Event)\n" +
            "WHERE review.score >= 4\n" +
            "MATCH (event)<-[:`:ORGANIZED`]-(organizer:Organizator)\n" +
            "MATCH (organizer)-[:`:ORGANIZED`]->(newEvent:Event)\n" +
            "WHERE NOT (user)-[:`:ATTENDS`|`:REVIEWS`]->(newEvent)\n" +
            "WITH user, COLLECT(DISTINCT newEvent) as organizerEvents\n" +
            "MATCH (user)-[:`:LIKES`]->(topic:Topic)\n" +
            "MATCH (topicEvent:Event)-[:`:IS_ABOUT`]->(topic)\n" +
            "WHERE NOT (user)-[:`:ATTENDS`|`:REVIEWS`]->(topicEvent)\n" +
            "WITH user, organizerEvents, COLLECT(DISTINCT topicEvent) AS topicEvents \n" +
            "WITH user, organizerEvents + topicEvents AS organizerTopicEvents\n" +
            "MATCH (user)-[:`:FRIENDSHIP`]->(friend:User)\n" +
            "WITH user, organizerTopicEvents, COLLECT(friend) AS friends\n" +
            "UNWIND organizerTopicEvents AS event\n" +
            "OPTIONAL MATCH (friend)-[badFriendReview:`:REVIEWS`]->(event)\n" +
            "WHERE badFriendReview.score < 2\n" +
            "WITH user, event, friends, badFriendReview\n" +
            "WHERE badFriendReview IS NULL\n" +
            "WITH user, COLLECT(DISTINCT event) as filteredEvents, friends\n" +
            "MATCH (friend)-[friendReview:`:REVIEWS`]->(friendEvent:Event)\n" +
            "WHERE friendReview.score >= 4\n" +
            "AND NOT (user)-[:`:ATTENDS`|`:REVIEWS`]->(friendEvent)\n" +
            "WITH user, filteredEvents, COLLECT(DISTINCT friendEvent) AS friendEvents\n" +
            "WITH user, filteredEvents + friendEvents AS allEvents\n" +
            "UNWIND allEvents AS event\n" +
            "WITH user, event, \n" +
            "6371 * 2 * \n" +
            "     atan2(\n" +
            "       sqrt(\n" +
            "         sin(radians(event.coordinates[1] - user.coordinates[1])/2)^2 +\n" +
            "         cos(radians(user.coordinates[1])) * cos(radians(event.coordinates[1])) *\n" +
            "         sin(radians(event.coordinates[0] - user.coordinates[0])/2)^2\n" +
            "       ),\n" +
            "       sqrt(\n" +
            "         1 - (\n" +
            "           sin(radians(event.coordinates[1] - user.coordinates[1])/2)^2 +\n" +
            "           cos(radians(user.coordinates[1])) * cos(radians(event.coordinates[1])) *\n" +
            "           sin(radians(event.coordinates[0] - user.coordinates[0])/2)^2\n" +
            "         )\n" +
            "       )\n" +
            "     ) AS distanceKm\n" +
            "WHERE distanceKm <= $distanceKm\n"+
            "AND event.startingDate > $startingDate\n" +
            "WITH event.eventName AS name, head(collect(event)) AS uniqueEvent\n"+
            "RETURN uniqueEvent\n"+
            "ORDER BY uniqueEvent.startingDate\n" +
            "LIMIT $limit")
    List<EventNode> suggestEvent(String userId, int distanceKm, LocalDate startingDate, int limit);
}
