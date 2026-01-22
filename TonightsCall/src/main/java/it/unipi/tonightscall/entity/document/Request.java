package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing an organizer request to join an organization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Field("id")
    private String id;

    @Field("username")
    private String username;
}
