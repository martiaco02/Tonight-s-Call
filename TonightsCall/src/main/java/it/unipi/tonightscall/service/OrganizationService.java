package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.MembersDTO;
import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.utilies.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizerRepository organizerRepository) {
        this.organizationRepository = organizationRepository;
        this.organizerRepository = organizerRepository;
    }


    public void addJoinRequest(String organizationId, String username) {

        Organizer me = organizerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));

        Organization toJoin = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization Not Found!"));


        for (Request r : toJoin.getPendingRequests()) {
            if (r.getId().equals(me.getId())) {
                throw new RuntimeException("Request already exists!");
            }
        }

        for (Members m : toJoin.getMembers()) {
            if (m.getId().equals(me.getId())) {
                throw new RuntimeException("Member already exists!");
            }
        }

        toJoin.getPendingRequests().add(
                new Request(
                        me.getId(),
                        me.getUsername()
                )
        );

        organizationRepository.save(toJoin);
    }

    public OrganizationDTO acceptJoinRequest(String newMemberId, String username) {

        Organization me = organizationRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Organization Not Found!"));

        Organizer newMember = organizerRepository.findById(newMemberId)
                .orElseThrow(() -> new RuntimeException("Organizer Not Found!"));

        List<Request> pending = me.getPendingRequests();

        if (!pending.removeIf(req -> req.getId().equals(newMemberId)))
            throw new RuntimeException("New Members Not Found");

        me.getMembers().add(new Members(newMember.getId(), newMember.getUsername(), newMember.getPassword()));
        newMember.getOrganizations().add(new OrganizationForLinking(me.getId(), me.getName()));

        Organization saved = organizationRepository.save(me);
        organizerRepository.save(newMember);

        return Mapper.mapOrganizationToDto(saved);
    }

    /**
     * Updates Organization data
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the Organization exists.</li>
     *      <li>Deletes the Organization from the Organizer's organizations.</li>
     *      <li>Saves the updated Organization to MongoDB.</li>
     * </ol>
     * </p>
     *
     * @param organizationID The ID of the Organization to update.
     * @param newOrganizationDTO The OrganizationDTO containing the new data.
     * @return The updated OrganizationDTO.
     * @throws RuntimeException If the Organization is not found.
     */

    @Transactional
    public OrganizationDTO updateOrganization(String organizationID, OrganizationDTO newOrganizationDTO){
        Organization oldOrganization = organizationRepository.findById(organizationID)
                .orElseThrow(() -> new RuntimeException("Organization Not Found!"));

        // organization's name can change because the authentication based on username
        // is done on the members of it (so on their usernames)

        if(newOrganizationDTO.getName() != null){ oldOrganization.setName(newOrganizationDTO.getName());}
        if(newOrganizationDTO.getVatNumber() != null){ oldOrganization.setVatNumber(newOrganizationDTO.getVatNumber());}
        if(newOrganizationDTO.getEmail() != null){ oldOrganization.setEmail(newOrganizationDTO.getEmail());}

        // updating document
        organizationRepository.save(oldOrganization);
        return Mapper.mapOrganizationToDto(oldOrganization);
    }


    /**
     * Deletes the pending request of an Organizer to join an Organization
     * <p>
     * This method:
     * <ol>
     *      <li>Checks if the Organizer exists.</li>
     *      <li>Checks if the Organization exists.</li>
     *      <li>Deletes the Organizer's request from the list of the Organization's pending requests.</li>
     *      <li>Saves the updated Organization to MongoDB.</li>
     * </ol>
     * </p>
     *
     * @param organizerID The ID of the Organizer.
     * @param organizationName The name of the Organization from whom the request must be deleted.
     * @throws RuntimeException If the organizer is not found or if the Organization is not found.
     */
    @Transactional
    public void deleteRequest(String organizerID, String organizationName) {
        Organizer organizer = organizerRepository.findById(organizerID)
                .orElseThrow(() -> new RuntimeException("Organizer not found!"));

        Organization organization =  organizationRepository.findByName(organizationName)
                .orElseThrow(() -> new RuntimeException("Organization not found!"));

        if(organization.getPendingRequests() != null && !organization.getPendingRequests().isEmpty()){
            for(int i=0; i<organization.getPendingRequests().size(); i++){
                if(organization.getPendingRequests().get(i).getId().equals(organizer.getId())){
                    organization.getPendingRequests().remove(i);
                    organizationRepository.save(organization);
                    break;
                }
            }
        }

    }
}
