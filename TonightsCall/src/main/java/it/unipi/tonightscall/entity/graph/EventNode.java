package it.unipi.tonightscall.entity.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Neo4j Node entity representing an Event.
 * <p>
 * This node is used in the graph to establish relationships between users,
 * topics, and locations for recommendation purposes.
 * </p>
 */
@Node("Event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventNode {

    /**
     * Unique identifier for the event node.
     * Usually matches the ID in the MongoDB "events" collection.
     */
    @Id
    private String id;

    /**
     * The name of the event.
     */
    private String eventName;

    /**
     * The date from  the event takes place.
     */
    private LocalDate startingDate;

    /**
     * The date when the event ends.
     */
    private LocalDate endingDate;

    /**
     * Geospatial coordinates of the event.
     * <p>
     * Format: List of Doubles, typically [Longitude, Latitude].
     * </p>
     */
    private List<Double> coordinates;

    @Relationship(type = ":IS_ABOUT", direction = Relationship.Direction.OUTGOING)
    private Set<TopicNode> categories = new HashSet<>();
}
