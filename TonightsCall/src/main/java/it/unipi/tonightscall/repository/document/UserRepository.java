package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.User;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities in MongoDB.
 * <p>
 * This interface extends {@link MongoRepository} to provide standard CRUD operations
 * (Create, Read, Update, Delete) and defines custom query methods derived from properties.
 * </p>
 */
public interface UserRepository extends MongoRepository<@NonNull User, @NonNull String> {

    /**
     * Retrieves a User entity by its username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the User if found, or empty if not.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username already exists in the database.
     * <p>
     * This method is particularly useful during registration to prevent duplicate usernames.
     * </p>
     *
     * @param username The username to check.
     * @return {@code true} if a user with the username exists, {@code false} otherwise.
     */
    boolean existsByUsername(String username);
}
