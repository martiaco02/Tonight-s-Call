package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Organizer;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * MongoDB Repository for managing {@link Organizer} entities.
 * <p>
 * This interface handles operations specific to individual organizers (people),
 * utilizing the polymorphic nature of the "Organizers" collection.
 * </p>
 */
public interface OrganizerRepository extends MongoRepository<@NonNull Organizer, @NonNull String> {

    /**
     * Finds an Organizer by their unique username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the Organizer if found.
     */
    Optional<Organizer> findByUsername(String username);

    /**
     * Checks if an Organizer with the given username already exists.
     *
     * @param username The username to check.
     * @return {@code true} if exists, {@code false} otherwise.
     */
    boolean existsByUsername(String username);

}
