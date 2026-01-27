package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded entity representing a lightweight link to an Organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationForLinking {

    /**
     * Unique identifier of the organization.
     */
    @Field("id")
    private String id;

    /**
     * Name of the organization.
     */
    @Field("name")
    private String name;
}
