package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface EventRepository extends MongoRepository<@NonNull Event, @NonNull String> {
    Page<@NonNull Event> findAll(Pageable page);
}
