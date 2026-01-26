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

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, String> organizationId, Authentication authentication) {
        try {
                organizationService.addJoinRequest(organizationId.get("organization_id"), authentication.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> accept(@RequestBody Map<String, String> newMemberId, Authentication authentication) {
        try {
            OrganizationDTO updated = organizationService.acceptJoinRequest(newMemberId.get("new_member_id"), authentication.getName());
            return ResponseEntity.ok().body(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganization(@PathVariable String id, @RequestBody OrganizationDTO organizationDTO, Authentication authentication) {
        try {
            Organization organization = organizationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization not found"));

            if(!organization.getName().equals(authentication.getName()))
                return ResponseEntity.status(403).body("Forbidden: You can only update your own profile.");

            OrganizationDTO updatedOrganization = organizationService.updateOrganization(id, organizationDTO);
            return ResponseEntity.ok(updatedOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{organizerID}/organization/{organizationID}/request")
    public ResponseEntity<?> deleteRequest(@PathVariable String organizerID, @PathVariable String organizationName, Authentication authentication) {
        try{
            Organizer organizer = organizerRepository.findById(organizerID)
                    .orElseThrow(() -> new RuntimeException("Organizer not found!"));

            if(!organizer.getUsername().equals(authentication.getName()))
                return ResponseEntity.status(403).body("Forbidden: You can only update your own profile.");

            Organization organization = organizationRepository.findByName(organizationName).orElseThrow(() -> new RuntimeException("Organization not found!"));

            organizationService.deleteRequest(organizerID, organizationName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
