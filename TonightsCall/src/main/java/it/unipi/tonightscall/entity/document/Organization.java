package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Entity representing an Organization (Company/Group).
 * <p>
 * Identified in the database by the type alias "ORGANIZATION".
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias("ORGANIZATION")
@EqualsAndHashCode(callSuper = true)
public class Organization extends AbstracOrganizer {

    /**
     * List of members belonging to this organization.
     */
    @Field("list_of_members")
    private List<Members> members;

    /**
     * List of pending join requests.
     */
    @Field("pending_requests")
    private List<Request> pendingRequests;

}
