package it.unipi.tonightscall.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Field("date_update")
    private LocalDate dateUpdate;

    @Field("avarage_age")
    private double averageAge;

    @Field("predicted_income")
    private double predictedIncome;

    @Field("origin_attenders")
    private Map<String, Integer> originAttenders;

    @Field("total_attenders")
    private int totalAttenders;
}
