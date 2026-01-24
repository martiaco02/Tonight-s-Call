package it.unipi.tonightscall.service;

import it.unipi.tonightscall.DTO.OrganizationDTO;
import it.unipi.tonightscall.entity.document.*;
import it.unipi.tonightscall.repository.document.OrganizationRepository;
import it.unipi.tonightscall.repository.document.OrganizerRepository;
import it.unipi.tonightscall.utilies.Mapper;
import org.springframework.stereotype.Service;

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
}
