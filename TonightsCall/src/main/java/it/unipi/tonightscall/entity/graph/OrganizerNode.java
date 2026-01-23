package it.unipi.tonightscall.entity.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Neo4j Node entity representing an Organizer.
 * <p>
 * Mapped to the label "Organizator" in the graph database.
 * Used to link events to their creators.
 * </p>
 */
@Node("Organizator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerNode {

    /**
     * Unique identifier for the organizer.
     */
    @Id
    private String id;

    /**
     * The username of the organizer.
     */
    private String username;

    @Relationship(type = ":ORGANIZED", direction = Relationship.Direction.OUTGOING)
    private Set<EventNode> organized = new HashSet<>();
}
