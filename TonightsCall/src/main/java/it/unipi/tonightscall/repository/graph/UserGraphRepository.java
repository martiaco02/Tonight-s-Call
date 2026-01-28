package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.UserNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * Neo4j Repository for managing {@link UserNode} entities.
 * <p>
 * Handles user nodes and their relationships (e.g., LIKES) in the social graph.
 * </p>
 */
public interface UserGraphRepository extends Neo4jRepository<@NonNull UserNode, @NonNull String> {

    /**
     * Removes a friendship relationship between two users.
     *
     * @param username       The username of the first user.
     * @param friendUsername The username of the second user.
     */
    @Query("MATCH (u:User {username: $username})-[r:`:FRIENDSHIP`]-(f:User {username: $friendUsername}) DELETE r")
    void deleteFriendship(String username, String friendUsername);

    /**
     * Removes an attendance relationship between a user and an event.
     *
     * @param userId  The unique identifier of the user.
     * @param eventId The unique identifier of the event.
     */
    @Query("MATCH (u:User {id: $userId})-[r:`:ATTENDS`]-(e:Event {id: $eventId}) DELETE r")
    void deleteAttendecy(String userId, String eventId);

    /**
     * Suggests potential new friends for a user using a multi-criteria graph traversal.
     * <p>
     * The algorithm identifies candidates through two paths:
     * <ol>
     *  <li>Friends of Friends: Users who are connected to the target user's direct friends
     * but are not yet connected to the target user.</li>
     *  <li>Shared Interests: Users who like the same {@code Topic} nodes as the target user.</li>
     * </ol>
     * </p>
     * <p>
     * The final result combines both groups, removes duplicates, and filters out the target user
     * and their existing friends.
     * </p>
     *
     * @param userId The ID of the user receiving suggestions.
     * @param limit  The maximum number of suggested users to return.
     * @return A list of {@link UserNode} representing potential connections.
     */
    @Query("MATCH (user:User {id: $userId})-[:`:FRIENDSHIP`]-(friend:User)\n" +
            "MATCH (friend)-[:`:FRIENDSHIP`]-(newFriend:User)\n" +
            "WHERE NOT (user)-[:`:FRIENDSHIP`]-(newFriend)\n" +
            "WITH user, COLLECT (DISTINCT newFriend) AS newFriends\n" +
            "MATCH (user)-[:`:LIKES`]->(topic:Topic)\n" +
            "MATCH (topic)<-[:`:LIKES`]-(topicFriend:User)\n" +
            "WHERE user.id <> topicFriend.id\n" +
            "AND NOT (user)-[:`:FRIENDSHIP`]-(topicFriend)\n" +
            "WITH newFriends, COLLECT (DISTINCT topicFriend) AS topicFriends\n" +
            "WITH newFriends + topicFriends AS allFriends\n" +
            "UNWIND allFriends as friend\n" +
            "RETURN DISTINCT friend\n" +
            "LIMIT $limit;")
    List<UserNode> suggestFriends(String userId, int limit);
}
