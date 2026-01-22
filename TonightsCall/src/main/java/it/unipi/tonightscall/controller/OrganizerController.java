package it.unipi.tonightscall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.service.ControllerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final ControllerService controllerService;

    public OrganizerController(ControllerService controllerService) {
        this.controllerService = controllerService;
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
            OrganizationDTO createdUser = controllerService.registerOrganization(organizationDTO, authentication.getName());
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
