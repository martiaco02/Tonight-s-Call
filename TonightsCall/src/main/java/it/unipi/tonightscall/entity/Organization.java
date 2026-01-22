package it.unipi.tonightscall.entity;

import it.unipi.tonightscall.DTO.MembersDTO;
import it.unipi.tonightscall.DTO.RequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias("ORGANIZATION")
@EqualsAndHashCode(callSuper = true)
public class Organization extends AbstracOrganizer {

    @Field("list_of_members")
    private List<Members> members;

    @Field("pending_requests")
    private List<Request> pendingRequests;

}
