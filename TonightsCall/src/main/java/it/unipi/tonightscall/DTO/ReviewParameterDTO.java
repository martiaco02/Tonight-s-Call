package it.unipi.tonightscall.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewParameterDTO {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("score")
    private int score;
}
