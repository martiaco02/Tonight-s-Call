package it.unipi.tonightscall.entity.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Neo4j Node entity representing a Topic (Interest/Category).
 * <p>
 * These nodes serve as bridges to connect users with similar interests
 * or users to events of a specific type.
 * </p>
 */
@Node("Topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicNode {

    /**
     * The unique name of the topic (e.g., "Rock", "Jazz").
     * Acts as the primary key for this node.
     */
    @Id
    private String topic;
}
