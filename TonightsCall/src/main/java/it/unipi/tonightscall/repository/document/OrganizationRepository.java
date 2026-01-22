    package it.unipi.tonightscall.repository.document;

    import it.unipi.tonightscall.entity.document.Organization;
    import lombok.NonNull;
    import org.springframework.data.mongodb.repository.MongoRepository;

    import java.util.Optional;

    /**
     * MongoDB Repository for managing {@link Organization} entities.
     * <p>
     * This interface specifically handles operations for Organizations (companies/groups).
     * Even though they share the "Organizers" collection with individual Organizers,
     * Spring Data MongoDB uses the type alias to ensure this repository only returns Organizations.
     * </p>
     */
    public interface OrganizationRepository extends MongoRepository<@NonNull Organization, @NonNull String> {

        /**
         * Retrieves an Organization by its registered name.
         * <p>
         * Since Organizations use their name as a unique identifier for login/registration
         * (instead of a username), this method is critical for authentication.
         * </p>
         *
         * @param name The name of the organization.
         * @return An {@link Optional} containing the Organization if found.
         */
        Optional<Organization> findByName(@NonNull String name);

        /**
         * Checks if an Organization with the given name already exists.
         * <p>
         * Used during registration to prevent duplicate organization names.
         * </p>
         *
         * @param name The name to check.
         * @return {@code true} if the name is already taken, {@code false} otherwise.
         */
        boolean existsByName(String name);
    }
