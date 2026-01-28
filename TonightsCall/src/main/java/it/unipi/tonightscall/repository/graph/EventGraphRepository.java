package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.EventNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

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
}
