package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "doc_employee", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocEmployee {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_EMPLOYEE_SEQ_GEN", sequenceName = "doc_employee_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_EMPLOYEE_SEQ_GEN")
    private Long id;
    private Boolean isVaccinatedFlu;
    private Boolean isVaccinatedCovid;
    private Boolean isDeleted;

    @OneToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    @JsonIgnore
    private ClsOrganization organization;

    @OneToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    private DocPerson person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_vaccinated_flu", nullable = true)
    public Boolean getIsVaccinatedFlu() {
        return isVaccinatedFlu;
    }

    public void setIsVaccinatedFlu(Boolean vaccinatedFlu) {
        isVaccinatedFlu = vaccinatedFlu;
    }

    @Basic
    @Column(name = "is_vaccinated_covid", nullable = true)
    public Boolean getIsVaccinatedCovid() {
        return isVaccinatedCovid;
    }

    public void setIsVaccinatedCovid(Boolean vaccinatedCovid) {
        isVaccinatedCovid = vaccinatedCovid;
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    public DocPerson getPerson() {
        return person;
    }

    public void setPerson(DocPerson person) {
        this.person = person;
    }

    @Basic
    @Column(name = "is_deleted", nullable = false)
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocEmployee that = (DocEmployee) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(isVaccinatedFlu, that.isVaccinatedFlu) &&
                Objects.equals(isVaccinatedCovid, that.isVaccinatedCovid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isVaccinatedFlu, isVaccinatedCovid);
    }
}
