package it.unipi.tonightscall.entity.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRelationship {

    @RelationshipId
    @GeneratedValue
    private Long id;

    private int score;

    @TargetNode
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EventNode event;
}
