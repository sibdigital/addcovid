package ru.sibdigital.addcovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.ClsDistrict;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsUser;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "doc_request_subsidy", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocRequestSubsidy {
    private int id;
    private String attachmentPath;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private Timestamp timeReview;
    private String reqBasis;
    private String resolutionComment;
    private Integer oldDepartmentId;
    private Object additionalAttributes;
    private Integer statusActivity;
    private ClsOrganization organization;
    private ClsDepartment department;
    private ClsSubsidyRequestStatus subsidyRequestStatus;
    private ClsUser processedUser;
    private ClsUser reassignedUser;
    private ClsSubsidy subsidy;
    private ClsDistrict district;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "attachment_path")
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
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
    @Column(name = "time_update")
    public Timestamp getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @Basic
    @Column(name = "time_review")
    public Timestamp getTimeReview() {
        return timeReview;
    }

    public void setTimeReview(Timestamp timeReview) {
        this.timeReview = timeReview;
    }

    @Basic
    @Column(name = "req_basis")
    public String getReqBasis() {
        return reqBasis;
    }

    public void setReqBasis(String reqBasis) {
        this.reqBasis = reqBasis;
    }

    @Basic
    @Column(name = "resolution_comment")
    public String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
    }

    @Basic
    @Column(name = "old_department_id")
    public Integer getOldDepartmentId() {
        return oldDepartmentId;
    }

    public void setOldDepartmentId(Integer oldDepartmentId) {
        this.oldDepartmentId = oldDepartmentId;
    }

    @Basic
    @Column(name = "additional_attributes")
    public Object getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Object additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Basic
    @Column(name = "status_activity")
    public Integer getStatusActivity() {
        return statusActivity;
    }

    public void setStatusActivity(Integer statusActivity) {
        this.statusActivity = statusActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocRequestSubsidy that = (DocRequestSubsidy) o;
        return id == that.id && Objects.equals(attachmentPath, that.attachmentPath) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(timeUpdate, that.timeUpdate) && Objects.equals(timeReview, that.timeReview) && Objects.equals(reqBasis, that.reqBasis) && Objects.equals(resolutionComment, that.resolutionComment) && Objects.equals(oldDepartmentId, that.oldDepartmentId) && Objects.equals(additionalAttributes, that.additionalAttributes) && Objects.equals(statusActivity, that.statusActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attachmentPath, timeCreate, timeUpdate, timeReview, reqBasis, resolutionComment, oldDepartmentId, additionalAttributes, statusActivity);
    }

    @ManyToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id", nullable = false)
    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    @ManyToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id", nullable = false)
    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "id_subsidy_request_status", referencedColumnName = "id", nullable = false)
    public ClsSubsidyRequestStatus getSubsidyRequestStatus() {
        return subsidyRequestStatus;
    }

    public void setSubsidyRequestStatus(ClsSubsidyRequestStatus subsidyRequestStatus) {
        this.subsidyRequestStatus = subsidyRequestStatus;
    }

    @ManyToOne
    @JoinColumn(name = "id_processed_user", referencedColumnName = "id")
    public ClsUser getProcessedUser() {
        return processedUser;
    }

    public void setProcessedUser(ClsUser processedUser) {
        this.processedUser = processedUser;
    }

    @ManyToOne
    @JoinColumn(name = "id_reassigned_user", referencedColumnName = "id")
    public ClsUser getReassignedUser() {
        return reassignedUser;
    }

    public void setReassignedUser(ClsUser reassignedUser) {
        this.reassignedUser = reassignedUser;
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
    @JoinColumn(name = "id_district", referencedColumnName = "id")
    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }
}
