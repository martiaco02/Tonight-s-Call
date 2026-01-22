package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDTO extends AbstracOrganizerDTO {

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("username")
    private String username;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("password")
    private String password;

    @JsonProperty("organizations")
    private List<OrganizationForLinkingDTO> organizations;

}


