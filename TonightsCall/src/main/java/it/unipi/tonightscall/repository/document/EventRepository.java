package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Event;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;


public interface EventRepository extends MongoRepository<@NonNull Event, @NonNull String> {
    Page<@NonNull Event> findAll(Pageable page);

    Page<@NonNull Event> findByCategoriesIn(Collection<List<String>> categories, Pageable pageable);
    Page<@NonNull Event> findByCategoriesAll(Collection<List<String>> categories, Pageable pageable);
}
