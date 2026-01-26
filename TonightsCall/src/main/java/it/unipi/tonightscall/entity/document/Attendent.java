package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

/**
 * Embedded Entity representing a user attending an event.
 * <p>
 * Contains details about the attendee, including personal info
 * and the specific ticket type associated with their participation.
 * </p>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendent {

    /**
     * Unique identifier of the attendee.
     */
    @Field("id")
    private String id;


    /**
     * The category or type of ticket purchased (e.g., Standard, VIP).
     */
    @Field("ticket_type")
    private String ticketType;

    /**
     * Contact email of the attendee.
     */
    @Field("email")
    private String email;

    /**
     * Username of the attendee.
     */
    @Field("username")
    private String username;

    /**
     * Name of the attendee's hometown.
     */
    @Field("home_town")
    private String homeTown;

    /**
     * Date of birth of the attendee.
     */
    @Field("date_of_birth")
    private LocalDate dateOfBirth;
}
