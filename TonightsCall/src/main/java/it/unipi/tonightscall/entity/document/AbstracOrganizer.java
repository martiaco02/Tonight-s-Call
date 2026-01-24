package it.unipi.tonightscall.entity.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Abstract Base Entity representing an Organizer in the MongoDB "Organizers" collection.
 * <p>
 * This class serves as the parent for both individual {@link Organizer} and {@link Organization} entities.
 * It uses MongoDB's inheritance mapping strategy.
 * </p>
 */
@Data
@Document(collection = "organizers")
public class AbstracOrganizer {

    /**
     * Primary key of the document.
     */
    @Id
    private String id;

    /**
     * Name of the organizer (or the organization).
     */
    @Field("name")
    private String name;

    /**
     * VAT Number (Value Added Tax) for fiscal purposes.
     */
    @Field("VAT_number")
    private String vatNumber;

    /**
     * Contact email address.
     */
    @Field("email")
    private String email;

    /**
     * List of events managed by this organizer/organization.
     * Stored as a list of embedded documents.
     */
    @Field("events")
    private List<EventOrganization> events;

    @Field("type")
    private String type;
}
