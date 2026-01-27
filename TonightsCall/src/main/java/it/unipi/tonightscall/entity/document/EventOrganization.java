package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing a brief summary of an event managed by an organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganization {
    /**
     * Unique identifier of the organization.
     */
    @Field("id")
    private String id;

    /**
     * Name of the event.
     */
    @Field("name")
    private String name;
}
