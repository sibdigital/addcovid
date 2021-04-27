package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_inspection", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationInspection {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_ORG_INSPECTION_SEQ_GEN", sequenceName = "reg_organization_inspection_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ORG_INSPECTION_SEQ_GEN")
    private Long id;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_organization", nullable = false)
    private ClsOrganization organization;

    public ClsOrganization getOrganization() {
        return organization;
    }
    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }


    @ManyToOne
    @JoinColumn(name = "id_control_authority", nullable = false)
    private ClsControlAuthority controlAuthority;
    public ClsControlAuthority getControlAuthority() {
        return controlAuthority;
    }
    public void setControlAuthority(ClsControlAuthority controlAuthority) {
        this.controlAuthority = controlAuthority;
    }

    @ManyToOne
    @JoinColumn(name = "id_inspection_result")
    private ClsInspectionResult inspectionResult;

    public ClsInspectionResult getInspectionResult() {
        return inspectionResult;
    }
    public void setInspectionResult(ClsInspectionResult inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    @Basic
    @Column(name = "date_of_inspection")
    private Date dateOfInspection;

    public Date getDateOfInspection() {
        return dateOfInspection;
    }
    public void setDateOfInspection(Date dateOfInspection) {
        this.dateOfInspection = dateOfInspection;
    }


    @Basic
    @Column(name = "comment")
    private String comment;

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
        RegOrganizationInspection that = (RegOrganizationInspection) o;
        return Objects.equals(id, that.id) && Objects.equals(organization, that.organization) && Objects.equals(inspectionResult, that.inspectionResult) && Objects.equals(dateOfInspection, that.dateOfInspection) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organization, inspectionResult, dateOfInspection, comment);
    }
}
