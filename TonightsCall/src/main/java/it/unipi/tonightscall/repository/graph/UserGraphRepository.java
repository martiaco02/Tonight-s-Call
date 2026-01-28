package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.UserNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * Neo4j Repository for managing {@link UserNode} entities.
 * <p>
 * Handles user nodes and their relationships (e.g., LIKES) in the social graph.
 * </p>
 */
public interface UserGraphRepository extends Neo4jRepository<@NonNull UserNode, @NonNull String> {

    @Query("MATCH (u:User {username: $username})-[r:`:FRIENDSHIP`]-(f:User {username: $friendUsername}) DELETE r")
    void deleteFriendship(String username, String friendUsername);

    @Query("MATCH (u:User {id: $userId})-[r:`:ATTENDS`]-(e:Event {id: $eventId}) DELETE r")
    void deleteAttendecy(String userId, String eventId);
}
