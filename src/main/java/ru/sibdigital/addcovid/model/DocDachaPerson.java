package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "doc_dacha_person", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class DocDachaPerson {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_DACHA_PERSON_SEQ_GEN", sequenceName = "doc_dacha_person_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_DACHA_PERSON_SEQ_GEN")
    private Integer id;
    private String lastname;
    private String firstname;
    private String patronymic;
    private Integer age;
    @ManyToOne
    @JoinColumn(name = "id_doc_dacha", referencedColumnName = "id", nullable = false)
    private DocDacha docDachaByIdDocDacha;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Basic
    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocDachaPerson that = (DocDachaPerson) o;
        return id == that.id &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(patronymic, that.patronymic) &&
                Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastname, firstname, patronymic, age);
    }

    public DocDacha getDocDachaByIdDocDacha() {
        return docDachaByIdDocDacha;
    }

    public void setDocDachaByIdDocDacha(DocDacha docDachaByIdDocDacha) {
        this.docDachaByIdDocDacha = docDachaByIdDocDacha;
    }

}
