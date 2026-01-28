package it.unipi.tonightscall.repository.graph;

import it.unipi.tonightscall.entity.graph.EventNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EventGraphRepository extends Neo4jRepository<@NonNull EventNode,@NonNull String> {

    /**
     * Delete all  :IS_ABOUT relationship outgoing from an Event
     *
     * @param eventId EventId
     */
    @Query("MATCH (e:Event {id: $eventId})-[r:`:IS_ABOUT`]->() DELETE r")
    void deleteAllIsAboutRelationships(String eventId);
}
