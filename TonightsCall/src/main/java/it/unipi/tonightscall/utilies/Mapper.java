package it.unipi.tonightscall.utilies;

import it.unipi.tonightscall.DTO.*;
import it.unipi.tonightscall.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for mapping objects between the Data Transfer Object (DTO) layer
 * and the Persistence (Entity) layer.
 * <p>
 * This class provides static methods to convert {@link UserDTO} to {@link User} and vice versa.
 * It manually handles nested objects such as HomeTown and ReviewEvents to ensure
 * precise control over the data structure.
 * </p>
 */
public class Mapper {

    /**
     * Converts a User Entity (Database object) into a UserDTO (API object).
     *
     * @param entity The User entity retrieved from MongoDB.
     * @return A UserDTO populated with the entity's data, or null if the input is null.
     */
    public static UserDTO mapUserToDto(User entity) {

        if (entity == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastname(entity.getLastname());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setInterests(entity.getInterests());
        dto.setFriends(entity.getFriends());

        HomeTown homeTownEntity = entity.getHomeTown();

        if  (homeTownEntity != null) {
            Location locationEntity = homeTownEntity.getLocation();

            if (locationEntity != null) {
                dto.setHomeTown(
                        new HomeTownDTO(
                                homeTownEntity.getName(),
                                new LocationDTO(
                                        locationEntity.getType(),
                                        locationEntity.getCoordinates()
                                )
                        )
                );
            }
           else {
               dto.setHomeTown(
                       new HomeTownDTO(
                               homeTownEntity.getName(),
                               null
                       )
               );
            }
        } else {
            dto.setHomeTown(null);
        }

        List<ReviewEvent> reviewedEvents = entity.getReviewedEvents();
        if (reviewedEvents != null) {
            List<ReviewEventDTO> reviewedEventsDTO = new ArrayList<>();
            for (ReviewEvent event : reviewedEvents) {
                reviewedEventsDTO.add(new  ReviewEventDTO(
                        event.getEventName(),
                        event.getScore()
                ));
            }
            dto.setReviewedEvents(reviewedEventsDTO);

        } else {
            dto.setReviewedEvents(null);
        }


        return dto;
    }

    /**
     * Converts a UserDTO (API object) into a User Entity (Database object).
     * <p>
     * <b>Note:</b> The password is NOT mapped here directly. It is usually handled
     * by the Service layer which encodes it before saving.
     * </p>
     *
     * @param dto The UserDTO received from the client.
     * @return A User entity ready to be saved/updated in MongoDB, or null if input is null.
     */
    public static User mapUserToEntity(UserDTO dto) {

        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLastname(dto.getLastname());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setInterests(dto.getInterests());
        entity.setFriends(dto.getFriends());

        if (dto.getHomeTown() != null) {
            HomeTown ht = new HomeTown();
            ht.setName(dto.getHomeTown().getName());

            if (dto.getHomeTown().getLoc() != null) {
                Location loc = new Location();
                loc.setType(dto.getHomeTown().getLoc().getType());
                loc.setCoordinates(dto.getHomeTown().getLoc().getCoordinates());
                ht.setLocation(loc);
            }
            entity.setHomeTown(ht);
        }

        if (dto.getReviewedEvents() != null) {
            List<ReviewEvent> events = new ArrayList<>();
            for (ReviewEventDTO revDto : dto.getReviewedEvents()) {
                ReviewEvent rev = new ReviewEvent();
                rev.setEventName(revDto.getEventName());
                rev.setScore(revDto.getScore());
                events.add(rev);
            }
            entity.setReviewedEvents(events);
        }

        return entity;
    }

    public static Organizer mapOrganizerToEntity(OrganizerDTO dto) {

        if (dto == null) {
            return null;
        }

        Organizer entity = new Organizer();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setName(dto.getName());
        entity.setVatNumber(dto.getVatNumber());
        entity.setEmail(dto.getEmail());

        List<EventOrganizationDTO> eventDto = dto.getEvents();
        if (eventDto != null) {
            List<EventOrganization> events = new ArrayList<>();
            for (EventOrganizationDTO evDto : eventDto) {
                EventOrganization ev = new EventOrganization(
                        evDto.getId(),
                        evDto.getName()
                );
                events.add(ev);
            }
            entity.setEvents(events);
        } else {
            entity.setEvents(null);
        }

        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPassword(dto.getPassword());

        List<OrganizationForLinkingDTO> organizationsDto = dto.getOrganizations();
        if (organizationsDto != null) {

            List<OrganizationForLinking> organizations = new ArrayList<>();
            for (OrganizationForLinkingDTO orgDto : organizationsDto) {
                OrganizationForLinking org = new OrganizationForLinking(
                    orgDto.getId(),
                    orgDto.getName()
                );
                organizations.add(org);
            }
            entity.setOrganizations(organizations);

        } else {
            entity.setOrganizations(null);
        }
        return entity;
    }

    public static OrganizerDTO mapOrganizerToDto(Organizer entity) {
        if  (entity == null) {
            return null;
        }

        OrganizerDTO dto = new OrganizerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVatNumber(entity.getVatNumber());
        dto.setEmail(entity.getEmail());
        dto.setType(entity.getType());

        List<EventOrganization> eventsEntity = entity.getEvents();
        if (eventsEntity != null) {
            List<EventOrganizationDTO> events = new ArrayList<>();
            for (EventOrganization ev : eventsEntity) {
                events.add(
                        new EventOrganizationDTO(
                                ev.getId(),
                                ev.getName()
                        )
                );
            }
            dto.setEvents(events);
        } else {
            dto.setEvents(null);
        }

        dto.setLastName(entity.getLastName());
        dto.setUsername(entity.getUsername());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPassword(entity.getPassword());

        List<OrganizationForLinking> organizationLinkingentity =  entity.getOrganizations();
        if (organizationLinkingentity != null) {
            List<OrganizationForLinkingDTO> organizations = new ArrayList<>();
            for (OrganizationForLinking org : organizationLinkingentity) {
                OrganizationForLinkingDTO orgDto = new OrganizationForLinkingDTO(
                        org.getId(),
                        org.getName()
                );
                organizations.add(orgDto);
            }
            dto.setOrganizations(organizations);
        } else {
            dto.setOrganizations(null);
        }

        return dto;
    }

    public static Organization mapOrganizationToEntity(OrganizationDTO organizationDto) {
        return null;
    }

    public static OrganizerDTO mapOrganizationToDto(Organization saved) {
        return null;
    }
}
