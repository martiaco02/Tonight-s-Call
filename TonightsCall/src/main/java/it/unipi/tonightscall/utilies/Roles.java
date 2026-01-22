package it.unipi.tonightscall.utilies;

/**
 * Utility class defining standard role constants for authorization.
 * <p>
 * These constants are used throughout the application (JWT generation, Security Config, etc.)
 * to ensure consistency in role naming and access control.
 * </p>
 */
public class Roles {

    /**
     * Role identifier for a standard User.
     * <p>
     * Users have access to basic features like viewing events, adding friends, and managing their own profile.
     * </p>
     */
    public static final String USER_ROLE = "USER";

    /**
     * Role identifier for an individual Organizer.
     * <p>
     * Organizers can create and manage events, as well as create Organizations.
     * </p>
     */
    public static final String ORGANIZER_ROLE = "ORGANIZER";

    /**
     * Role identifier for an Organization entity.
     * <p>
     * Used when an authenticated session represents a company or group acting as a single entity,
     * often managed by one of its members.
     * </p>
     */
    public static final String ORGANIZATION_ROLE = "ORGANIZATION";

    private Roles() {
        throw new IllegalStateException("Utility class");
    }
}
