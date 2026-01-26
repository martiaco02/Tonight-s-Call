package it.unipi.tonightscall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.tonightscall.DTO.EventDTO;
import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.DTO.OrganizerDTO;
import it.unipi.tonightscall.entity.document.Organization;
import it.unipi.tonightscall.entity.document.OrganizationForLinking;
import it.unipi.tonightscall.entity.document.Organizer;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling operations specific to Organizers.
 * <p>
 * This controller allows authenticated organizers to perform management tasks,
 * such as registering new organizations under their control.
 * </p>
 */
@RestController
@RequestMapping("/organizer")
@Tag(name = "Organizer Operations", description = "Endpoints for actions performed by authenticated Organizers")
public class OrganizerController {

    private final OrganizerService controllerService;
    private final OrganizerRepository organizerRepository;
    private final OrganizationRepository organizationRepository;

    public OrganizerController(OrganizerService controllerService, OrganizerRepository organizerRepository, OrganizationRepository organizationRepository) {
        this.controllerService = controllerService;
        this.organizerRepository = organizerRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Registers a new Organization linked to the currently authenticated Organizer.
     *
     * @param organizationDTO The details of the organization to be created.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The created OrganizationDTO if successful, or an error message otherwise.
     */
    @Operation(
            summary = "Register a new managed Organization",
            description = "Allows a logged-in Organizer to register a new Organization. The Organization will be linked to the Organizer's account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organization registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or creation failed",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized or not logged in)",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/registerOrganization")
    public ResponseEntity<?> registerOrganization(@RequestBody OrganizationDTO organizationDTO, Authentication authentication) {
        try {
            OrganizationDTO createdOrganization = controllerService.registerOrganization(organizationDTO, authentication.getName());
            return ResponseEntity.ok(createdOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registers a new Event created by the authenticated Organizer.
     *
     * @param eventDTO       The details of the event to be created.
     * @param authentication The security context containing the current user's details.
     * @return The created EventDTO if successful.
     */
    @Operation(
            summary = "Create a new Event",
            description = "Allows an authenticated Organizer (or Organization) to create and publish a new event."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or creation failed",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (User is not authorized)",
                    content = @Content(mediaType = "text/plain")
            )
    })

    /**
     * Registers a new Event made by the currently authenticated Organizer.
     *
     * @param eventDTO The details of the event to be created.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The created EventDTO if successful, or an error message otherwise.
     */
    @PostMapping("/registerEvent")
    public ResponseEntity<?> registerEvent(@RequestBody EventDTO eventDTO, Authentication authentication) {
        try{
            EventDTO createdEvent = controllerService.registerEvent(
                    eventDTO,
                    authentication.getName(),
                    authentication.getAuthorities().iterator().next().getAuthority());
            return ResponseEntity.ok(createdEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates the data of the authenticated Organizer.
     *
     * @param organizerDTO The updated details of the organizer to be updated.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizerDTO if successful, or an error message otherwise.
     */

    @PutMapping
    public ResponseEntity<?> updateOrganizer(@RequestBody OrganizerDTO organizerDTO, Authentication authentication) {
        try{
            OrganizerDTO updatedOrganizer = controllerService.updateOrganizer(authentication.getName(), organizerDTO);
            return ResponseEntity.ok(updatedOrganizer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Deletes a membership from the Organization's member list.
     *
     * @param organizationName The name of the Organization the authenticated Organizer wants to delete their membership from.
     * @param authentication  The security context containing the current user's details (injected by Spring Security).
     * @return The updated OrganizerDTO if successful, or an error message otherwise.
     */


    @DeleteMapping("organization/{organizationID}")
    public ResponseEntity<?> deleteOrganizationMembership(@PathVariable String organizationName, Authentication authentication) {
        try{
            OrganizerDTO organizerDTO = controllerService.deleteOrganizationMembership(authentication.getName(), organizationName);
            return ResponseEntity.ok(organizerDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

}
