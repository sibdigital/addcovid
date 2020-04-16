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
                .lastname(this.lastname != null ? this.lastname.trim() : this.lastname)
                .firstname(this.firstname != null ? this.firstname.trim() : this.firstname)
                .patronymic(this.patronymic != null ? this.patronymic.trim() : this.patronymic)
                .age(this.age)
                .build();
    }
}
