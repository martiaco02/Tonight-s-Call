package it.unipi.tonightscall.repository;

import it.unipi.tonightscall.entity.AbstracOrganizer;
import it.unipi.tonightscall.entity.Organizer;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AbstractOrganizerRepository extends MongoRepository<@NonNull AbstracOrganizer, @NonNull String> {

    Optional<AbstracOrganizer> findByName(String id);

    boolean existsByName(String name);

}
