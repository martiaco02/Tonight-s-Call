package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<@NonNull Event, @NonNull String> {
}
