package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.EventNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EventGraphRepository extends Neo4jRepository<@NonNull EventNode,@NonNull String> {
}
