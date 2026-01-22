package it.unipi.tonightscall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeAlias("ORGANIZER")
@NoArgsConstructor
@AllArgsConstructor
public class Organizer extends AbstracOrganizer{

    @Field("lastname")
    private String lastName;

    @Field("username")
    private String username;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("password")
    private String password;

    @Field("organizations")
    private List<OrganizationForLinking> organizations;

}
