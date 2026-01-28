package it.unipi.tonightscall.entity.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Neo4j Node entity representing a User.
 * <p>
 * Contains user identification, location data, and outgoing relationships
 * to topics they are interested in.
 * </p>
 */
@Node("User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNode {

    /**
     * Unique identifier for the user.
     * Matches the User ID in MongoDB.
     */
    @Id
    private String id;

    /**
     * The user's username.
     */
    private String username;

    /**
     * The user's email.
     */
    private String email;

    /**
     * The user's current or home coordinates.
     * Format: [Longitude, Latitude].
     */
    private List<Double> coordinates;

    /**
     * Outgoing relationship indicating the topics the user likes.
     * <p>
     * Direction: User -> Topic
     * Relationship Type: "LIKES"
     * </p>
     */
    @Relationship(type = ":LIKES", direction = Relationship.Direction.OUTGOING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TopicNode> interests = new HashSet<>();

    @Relationship(type = ":FRIENDSHIP", direction = Relationship.Direction.OUTGOING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserNode> friends = new HashSet<>();

    @Relationship(type = ":FRIENDSHIP", direction = Relationship.Direction.INCOMING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserNode> friendsIn = new HashSet<>();

    @Relationship(type = ":ATTENDS", direction = Relationship.Direction.OUTGOING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<EventNode> attendees = new HashSet<>();

    @Relationship(type = ":REVIEWS", direction = Relationship.Direction.OUTGOING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ReviewRelationship> reviews = new ArrayList<>();
}
