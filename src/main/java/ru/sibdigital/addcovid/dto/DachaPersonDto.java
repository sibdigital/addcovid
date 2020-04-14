package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocDacha;
import ru.sibdigital.addcovid.model.DocDachaPerson;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DachaPersonDto {
    private String lastname;
    private String firstname;
    private String patronymic;
    private Integer age;

    public DocDachaPerson convertToDocDachaPerson(){
        return DocDachaPerson.builder()
                .lastname(this.lastname)
                .firstname(this.firstname)
                .patronymic(this.patronymic)
                .age(this.age)
                .build();
    }
}
