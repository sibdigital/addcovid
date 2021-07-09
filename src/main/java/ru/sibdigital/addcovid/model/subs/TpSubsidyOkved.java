package ru.sibdigital.addcovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tp_subsidy_okved", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TpSubsidyOkved {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "tp_subsidy_okved_id_seq", sequenceName = "tp_subsidy_okved_id_seq",
            allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tp_subsidy_okved_id_seq")
    private Long id;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private Integer idTypeOrganization;

    private ClsSubsidy subsidy;

    private Okved okved;

    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "time_create")
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "id_type_organization")
    public Integer getIdTypeOrganization() {
        return idTypeOrganization;
    }

    public void setIdTypeOrganization(Integer idTypeOrganization) {
        this.idTypeOrganization = idTypeOrganization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpSubsidyOkved that = (TpSubsidyOkved) o;
        return id == that.id && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(idTypeOrganization, that.idTypeOrganization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, idTypeOrganization);
    }

    @ManyToOne
    @JoinColumn(name = "id_subsidy", referencedColumnName = "id", nullable = false)
    public ClsSubsidy getSubsidy() {
        return subsidy;
    }

    public void setSubsidy(ClsSubsidy subsidy) {
        this.subsidy = subsidy;
    }

    @ManyToOne
    @JoinColumn(name = "id_okved", referencedColumnName = "id", nullable = false)
    public Okved getOkved() {
        return okved;
    }

    public void setOkved(Okved okved) {
        this.okved = okved;
    }
}
