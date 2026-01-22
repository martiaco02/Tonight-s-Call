package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing a member of an organization.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Members {

    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("password")
    private String password;

}
