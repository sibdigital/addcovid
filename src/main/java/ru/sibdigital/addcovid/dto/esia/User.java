package ru.sibdigital.addcovid.dto.esia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String firstName;
    private String lastName;
    private String patronymic;
    private Boolean trusted;
    private String inn;
    private String email;
    private String status;
    private Boolean verifying;
    private SelfEmployed selfEmployed;

    public class SelfEmployed {
        private Boolean confirmed;
        private Date confirmedDate;

        public Boolean getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(Boolean confirmed) {
            this.confirmed = confirmed;
        }
    }

    public String getFIO() {
        String fio = this.lastName != null ? this.lastName : "";
        fio += this.firstName != null ? " " + this.firstName : "";
        fio += this.patronymic != null ? " " + this.patronymic : "";
        return fio;
    }
}
