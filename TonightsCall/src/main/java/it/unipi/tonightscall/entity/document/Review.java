package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Review {

    @Field("score")
    private int score;

    @Field("username")
    private String username;

    @Field("text")
    private String text;
}
