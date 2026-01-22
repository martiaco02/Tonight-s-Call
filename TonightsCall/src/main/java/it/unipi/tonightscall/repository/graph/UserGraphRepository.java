package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.UserNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * Neo4j Repository for managing {@link UserNode} entities.
 * <p>
 * Handles user nodes and their relationships (e.g., LIKES) in the social graph.
 * </p>
 */
public interface UserGraphRepository extends Neo4jRepository<@NonNull UserNode, @NonNull String> {
}
