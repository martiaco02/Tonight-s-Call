package it.unipi.tonightscall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Members {

    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("password")
    private String password;

}
