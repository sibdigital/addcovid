package ru.sibdigital.addcovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.ClsFileType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tp_required_subsidy_file", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TpRequiredSubsidyFile {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "tp_required_subsidy_file_id_seq", sequenceName = "tp_required_subsidy_file_id_seq",
            allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tp_required_subsidy_file_id_seq")
    private Long id;
    private Long idSubsidy;
    private Boolean isDeleted;
    private Boolean isRequired;
    private Timestamp timeCreate;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "id_file_type", referencedColumnName = "id")
    private ClsFileType clsFileType;

    public ClsFileType getClsFileType() {return clsFileType;}

    public void setClsFileType(ClsFileType clsFileType) {this.clsFileType = clsFileType;}

    @Basic
    @Column(name = "id_subsidy")
    public Long getIdSubsidy() {
        return idSubsidy;
    }

    public void setIdSubsidy(Long id_subsidy) {this.idSubsidy = id_subsidy;}


    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "is_required")
    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
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
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpRequiredSubsidyFile that = (TpRequiredSubsidyFile) o;
        return id == that.id && idSubsidy == that.idSubsidy && Objects.equals(clsFileType,that.clsFileType) && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(isRequired, that.isRequired) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idSubsidy, clsFileType, isDeleted, isRequired, timeCreate, comment);
    }
}
