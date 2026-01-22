package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing a summary of an event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganization {
    @Field("id")
    private String id;

    @Field("name")
    private String name;
}
