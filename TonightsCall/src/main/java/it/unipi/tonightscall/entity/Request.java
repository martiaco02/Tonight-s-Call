package it.unipi.tonightscall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Field("id")
    private String id;

    @Field("username")
    private String username;
}
