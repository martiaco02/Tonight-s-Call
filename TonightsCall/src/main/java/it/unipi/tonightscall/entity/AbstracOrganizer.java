package it.unipi.tonightscall.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "Organizers")
public class AbstracOrganizer {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("VAT_number")
    private String vatNumber;

    @Field("email")
    private String email;

    @Field("events")
    private List<EventOrganization> events;

    private String type;

}
