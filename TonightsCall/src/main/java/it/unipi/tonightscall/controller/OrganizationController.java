package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.entity.document.Organization;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.service.OrganizationService;
import it.unipi.tonightscall.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * REST Controller handling operations specific to Organization.
 */
@RestController
@RequestMapping("/organization")
@Tag(name = "Organization", description = "Endpoints for Organizations' operations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizerService organizerService;
    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;

    public OrganizationController(
            OrganizationService organizationService,
            OrganizerService organizerService,
            OrganizationRepository organizationRepository,
            OrganizerRepository organizerRepository
    ) {
        this.organizationService = organizationService;
        this.organizerService = organizerService;
        this.organizationRepository = organizationRepository;
        this.organizerRepository = organizerRepository;
    }


    /**
     * Registers an Organizer's new Request to join an Organization
     *
     * @param organizationId The id of the Organization.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return An "OK" entity, or an error message otherwise.
     */

    @Operation(summary = "Registers a new Request", description = "Creates a new pending join request in the organization's list.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Request registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, request already exists or Organization's member already exists.",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, String> organizationId, Authentication authentication) {
        try {
            organizationService.addJoinRequest(organizationId.get("organization_id"), authentication.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //TODO: response entity
    @GetMapping
    public List<Organization> getAllOrganizations() { return this.organizationService.getAllOrganizations(); }

    //TODO: response entity
    @GetMapping("/{id}")
    public Optional<Organization> getOrganizationById(@PathVariable String id) { return this.organizationService.getOrganizationById(id); }


    /**
     * Accepts a join Request of an Organizer.
     *
     * @param newMemberId The id of the Organizer who asked to join the Organization.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizationDTO, or an error message otherwise.
     */
    @Operation(summary = "Accepts a Request", description = "Accepts a pending join request in the organization's list.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Request accepted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, Organization or Organizer are not found or their pending request is not found.",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })

    @PostMapping("/accept")
    public ResponseEntity<?> accept(@RequestBody Map<String, String> newMemberId, Authentication authentication) {
        try {
            OrganizationDTO updated = organizationService.acceptJoinRequest(newMemberId.get("new_member_id"), authentication.getName());
            return ResponseEntity.ok().body(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updated an Organization data.
     *
     * @param organizationDTO The OrganizationDTO containing the updated data.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizationDTO, or an error message otherwise.
     */

    @Operation(summary = "Updates an Organization data", description = "Updates information about an existing Organization.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organization updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or Organization is not found.",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })

    @PutMapping
    public ResponseEntity<?> updateOrganization(@RequestBody OrganizationDTO organizationDTO, Authentication authentication) {
        try {
            OrganizationDTO updatedOrganization = organizationService.updateOrganization(authentication.getName(), organizationDTO);
            return ResponseEntity.ok(updatedOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes an Organizer Request to join the Organization.
     *
     * @param orgUsername The Organizer Username whose Request to join must be refused.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizationDTO, or an error message otherwise.
     */
    @Operation(summary = "Deletes a Request", description = "Removes a pending join request from the organization's list.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Request removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, Organization or Organizer whose request must be removed are not found.",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })

    @DeleteMapping("/request/{orgUsername}")
    public ResponseEntity<?> deleteRequest(@PathVariable String orgUsername, Authentication authentication) {
        try{
            OrganizationDTO organizationDTO = organizationService.deleteRequest(orgUsername, authentication.getName());
            return ResponseEntity.ok(organizationDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
