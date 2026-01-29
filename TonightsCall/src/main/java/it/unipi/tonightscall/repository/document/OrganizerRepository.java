package it.unipi.tonightscall.repository.document;

import it.unipi.tonightscall.entity.document.Organizer;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository for managing {@link Organizer} entities.
 * <p>
 * This interface handles operations specific to individual organizers (people),
 * utilizing the polymorphic nature of the "Organizers" collection.
 * </p>
 */
public interface OrganizerRepository extends MongoRepository<@NonNull Organizer, @NonNull String> {

    /**
     * Finds an Organizer by their unique username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the Organizer if found.
     */
    Optional<Organizer> findByUsername(String username);

    /**
     * Checks if an Organizer with the given username already exists.
     *
     * @param username The username to check.
     * @return {@code true} if exists, {@code false} otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Find all the Organizer or all the Organization
     * @param type ORGANIZER or ORGANIZATION
     * @param pageable the page for pagination
     * @return the page
     */
    Page<@NonNull Organizer> findByType(String type, Pageable pageable);

    /**
     * Executes a MongoDB aggregation pipeline to calculate event statistics.
     * <p>
     * The pipeline performs the following steps:
     * <ol>
     *      <li>$match: Selects the organization document itself and all organizer documents linked to it </li>
     *      <li>$group: Identifies the maximum event count for the organization and collects organizer data into a list </li>
     *      <li>$project: Maps the results into a final structure calculating individual event counts</li>
     * </ol>
     * and concatenating organizer names.
     * </p>
     *
     * @param organizationId The ID of the organization to analyze.
     * @return A list of maps containing 'username', 'fullname', 'organizerEventsCount', and 'organizationTotalEvents'.
     */
    @Aggregation(pipeline = {
        """
        {
            $match: {
                $or: [
                    { "type": "ORGANIZER", "organizations.id": ?0 },
                    { "type": "ORGANIZATION", "_id": ?0 }
                ]
            }
        }
        """,
        """
        {
            $group: {
                _id: null,
                organizationTotalEvents: {
                    $max: {
                        $cond: [
                            { $eq: ["$type", "ORGANIZATION"] },
                            { $size: { $ifNull: ["$events", []] } },
                            0
                        ]
                    }
                },
                list: {
                    $push: {
                        $cond: [
                            { $eq: ["$type", "ORGANIZER"] },
                            {
                                username: "$username",
                                name: "$name",
                                lastname: "$lastname",
                                events: "$events"
                            },
                            "$$REMOVE"
                        ]
                    }
                }
            }
        }
        """,
        """

                {
            $project: {
                _id: 0,
                analysis: {
                    $map: {
                        input: "$list",
                        as: "user",
                        in: {
                            username: "$$user.username",
                            fullname: { $concat: ["$$user.name", " ", "$$user.lastname"] },
                            organizerEventsCount: { $size: { $ifNull: ["$$user.events", []] } },
                            organizationTotalEvents: "$organizationTotalEvents"
                        }
                    }
                }
            }
        }
        """
    })
    List<HashMap<?, ?>> getOrganizationAnalisys(String organizationId);
}
