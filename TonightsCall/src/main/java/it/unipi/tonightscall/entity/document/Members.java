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

    /**
     * Unique identifier of the member.
     */
    @Field("id")
    private String id;

    /**
     * Member's name (ex. Mario Rossi).
     */
    @Field("name")
    private String name;

    /**
     * Member's password.
     */
    @Field("password")
    private String password;

}
