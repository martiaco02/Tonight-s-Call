package it.unipi.tonightscall.entity.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.util.List;

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
     * The date on which the event takes place.
     */
    private LocalDate date;

    /**
     * Geospatial coordinates of the event.
     * <p>
     * Format: List of Doubles, typically [Longitude, Latitude].
     * </p>
     */
    private List<Double> coordinates;
}
