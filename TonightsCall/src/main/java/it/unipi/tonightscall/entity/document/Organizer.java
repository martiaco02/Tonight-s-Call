package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity representing an individual Organizer (Person).
 * <p>
 * Identified in the database by the type alias "ORGANIZER".
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TypeAlias("ORGANIZER")
@NoArgsConstructor
@AllArgsConstructor
public class Organizer extends AbstracOrganizer{

    /**
     * Last name of the individual.
     */
    @Field("lastname")
    private String lastName;

    /**
     * Unique username for login.
     */
    @Field("username")
    private String username;

    /**
     * Date of birth of the organizer.
     */
    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Hashed password for authentication.
     */
    @Field("password")
    private String password;

    /**
     * List of references to Organizations this individual is part of.
     */
    @Field("organizations")
    private List<OrganizationForLinking> organizations;

}
