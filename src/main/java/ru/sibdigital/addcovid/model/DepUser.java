package ru.sibdigital.addcovid.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "dep_user", schema = "public", catalog = "addcovid")
@AllArgsConstructor
@NoArgsConstructor
public class DepUser implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DEP_USR_SEQ_GEN", sequenceName = "dep_user_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEP_USR_SEQ_GEN")
    private Long id;

    private Long idDepartment;




    private String lastname;
    private String firstname;
    private String patronymic;
    private String login;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    @Basic
    @Column(name = "lastname", nullable = false, length = 100)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    @Basic
    @Column(name = "firstname", nullable = false, length = 100)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    @Basic
    @Column(name = "patronymic", nullable = true, length = 100)
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Basic
    @Column(name = "login", nullable = false, length = 100)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepUser depUser = (DepUser) o;
        return Objects.equals(id, depUser.id) &&
                Objects.equals(lastname, depUser.lastname) &&
                Objects.equals(firstname, depUser.firstname) &&
                Objects.equals(patronymic, depUser.patronymic) &&
                Objects.equals(login, depUser.login) &&
                Objects.equals(password, depUser.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastname, firstname, patronymic, login, password);
    }
}
