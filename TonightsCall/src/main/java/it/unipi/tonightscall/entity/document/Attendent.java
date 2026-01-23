package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendent {

    @Field("id")
    private String id;

    @Field("ticket_type")
    private String ticketType;

    @Field("email")
    private String email;

    @Field("username")
    private String username;

    @Field("home_town")
    private String homeTown;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;
}
