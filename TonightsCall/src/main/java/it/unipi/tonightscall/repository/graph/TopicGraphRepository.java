package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.TopicNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * Neo4j Repository for managing {@link TopicNode} entities.
 * <p>
 * Handles the persistence of Topic/Interest nodes used for categorizing events
 * and user preferences.
 * </p>
 */
public interface TopicGraphRepository extends Neo4jRepository<@NonNull TopicNode,@NonNull String> {
}
