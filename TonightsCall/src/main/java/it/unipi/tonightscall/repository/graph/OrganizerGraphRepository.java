package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.OrganizerNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

/**
 * Neo4j Repository for managing {@link OrganizerNode} entities.
 * <p>
 * Handles the persistence of Organizer nodes in the graph database,
 * typically used for linking events to their creators.
 * </p>
 */
public interface OrganizerGraphRepository extends Neo4jRepository<@NonNull OrganizerNode,@NonNull String> {
}
