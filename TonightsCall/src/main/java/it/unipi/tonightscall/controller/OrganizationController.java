package it.unipi.tonightscall.controller;

import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
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
}
