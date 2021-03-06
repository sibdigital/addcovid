package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "doc_person", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocPerson {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_PERSON_SEQ_GEN", sequenceName = "doc_person_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_PERSON_SEQ_GEN")
    private Long id;


    @Basic
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Basic
    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Basic
    @Column(name = "patronymic", nullable = true, length = 100)
    private String patronymic;

    @Basic
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;



/*
    @Basic
    @Column(name = "is_agree", nullable = false)
    private Boolean isAgree;
*/

    @ManyToOne
    @JoinColumn(name="id_request", nullable=true)
    @JsonIgnore
    private DocRequest docRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_organization", nullable=false)
    @JsonIgnore
    private ClsOrganization organization;


/*    @SequenceGenerator(name = "PERSON_SEQ", sequenceName = "doc_person_id_seq")*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
    /*
    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }
*/

    public DocRequest getDocRequest() {
        return docRequest;
    }

    public void setDocRequest(DocRequest docRequest) {
        this.docRequest = docRequest;
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocPerson docPerson = (DocPerson) o;
        return Objects.equals(id, docPerson.id) &&
                Objects.equals(lastname, docPerson.lastname) &&
                Objects.equals(firstname, docPerson.firstname) &&
                Objects.equals(patronymic, docPerson.patronymic);
                //Objects.equals(isAgree, docPerson.isAgree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastname, firstname, patronymic/*, isAgree*/);
    }


}
