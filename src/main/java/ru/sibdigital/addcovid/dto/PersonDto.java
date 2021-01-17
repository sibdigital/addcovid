package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocPerson;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PersonDto {
    private Long id;
    private String lastname;
    private String firstname;
    private String patronymic;
    //private Boolean isAgree;
    private String status;
    private boolean isDeleted;

   public DocPerson convertToPersonEntity(){
       return DocPerson.builder().firstname(this.firstname)
               .lastname(this.lastname)
               //.isAgree(this.isAgree)
               .patronymic(this.patronymic)
               .isDeleted(this.isDeleted)
               .build();
   }

    public String getFIO() {
       String fio = "";
       if (Objects.nonNull(this.lastname)) {
           fio += this.lastname;
       }
       if (Objects.nonNull(this.firstname) && !this.lastname.isEmpty()) {
           fio += ' ' + this.firstname;
       }
       if (this.patronymic != null && !this.patronymic.isEmpty()) {
           fio += ' ' + this.patronymic;
       }
       return fio;
   }

}
