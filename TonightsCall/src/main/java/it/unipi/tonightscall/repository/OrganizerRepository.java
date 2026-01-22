package it.unipi.tonightscall.repository;

import it.unipi.tonightscall.entity.Organizer;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrganizerRepository extends MongoRepository<@NonNull Organizer, @NonNull String> {

    Optional<Organizer> findByUsername(String username);
    boolean existsByUsername(String username);

}
