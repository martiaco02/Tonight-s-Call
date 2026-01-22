package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.AbstracOrganizer;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * MongoDB Repository for the abstract base class {@link AbstracOrganizer}.
 * <p>
 * This repository allows performing queries across the entire "Organizers" collection,
 * retrieving documents regardless of whether they are individual {@code Organizer}s
 * or {@code Organization}s.
 * </p>
 */
public interface AbstractOrganizerRepository extends MongoRepository<@NonNull AbstracOrganizer, @NonNull String> {

    /**
     * Finds an entity (Organizer or Organization) by its name.
     *
     * @param name The Id to search for.
     * @return An {@link Optional} containing the entity if found.
     */
    Optional<AbstracOrganizer> findByName(String name);

    /**
     * Checks if an entity with the given name already exists.
     *
     * @param name The name to check.
     * @return {@code true} if exists, {@code false} otherwise.
     */
    boolean existsByName(String name);

}
