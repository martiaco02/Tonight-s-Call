package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.DTO.OrganizerDTO;
import it.unipi.tonightscall.entity.document.AbstracOrganizer;
import it.unipi.tonightscall.entity.document.Organization;
import it.unipi.tonightscall.entity.document.Organizer;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.service.OrganizationService;
import it.unipi.tonightscall.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * REST Controller handling operations specific to Organization.
 */
@RestController
@RequestMapping("/organization")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizerService organizerService;
    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;

    public OrganizationController(OrganizationService organizationService, OrganizerService organizerService, OrganizationRepository organizationRepository, OrganizerRepository organizerRepository) {
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

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, String> organizationId, Authentication authentication) {
        try {
            organizationService.addJoinRequest(organizationId.get("organization_id"), authentication.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Accepts a join Request of an Organizer.
     *
     * @param newMemberId The id of the Organizer who asked to join the Organization.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizationDTO, or an error message otherwise.
     */
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
