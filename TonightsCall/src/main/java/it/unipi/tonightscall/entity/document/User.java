package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

/**
 * MongoDB Entity representing a User.
 * <p>
 * This class maps directly to a document inside the "users" collection in MongoDB.
 * It stores personal details, authentication credentials, and embedded social information.
 * </p>
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * The primary key of the document.
     * <p>
     * MongoDB automatically generates this ID if not provided.
     * </p>
     */
    @Id
    private String id;

    /**
     * The first name of the user.
     */
    @Field("name")
    private String name;

    /**
     * The last name of the user.
     */
    @Field("lastname")
    private String lastname;

    /**
     * The user's email address.
     */
    @Field("email")
    private String email;

    /**
     * The unique username.
     */
    @Field("username")
    private String username;

    /**
     * The user's password.
     * <p>
     * <b>Security Note:</b> This is stored in an encrypted version of the password, never plain text.
     * </p>
     */
    @Field("password")
    private String password;

    /**
     * The user's date of birth.
     * Mapped to the "date_of_birth" field in MongoDB.
     */
    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * List of interests or tags associated with the user.
     */
    @Field("interests")
    private List<String> interests;

    /**
     * List of references (IDs or Usernames) to the user's friends.
     */
    @Field("friends")
    private List<String> friends;

    /**
     * Embedded document representing the user's hometown.
     * Mapped to the "home_town" field in MongoDB.
     */
    @Field("home_town")
    private Address address;

    /**
     * List of embedded documents representing reviews written by the user.
     * Mapped to the "reviewed_events" field in MongoDB.
     */
    @Field("reviewed_events")
    private List<ReviewEvent> reviewedEvents;

}