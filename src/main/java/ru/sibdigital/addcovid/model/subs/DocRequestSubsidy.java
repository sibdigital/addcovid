package ru.sibdigital.addcovid.model.subs;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.sibdigital.addcovid.model.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "doc_request_subsidy", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class DocRequestSubsidy {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "doc_request_subsidy_id_seq", sequenceName = "doc_request_subsidy_id_seq",
            allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_request_subsidy_id_seq")
    private Long id;
    private String attachmentPath;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private Timestamp timeReview;
    private Timestamp timeSend;
    private String reqBasis;
    private String resolutionComment;
    private Long oldDepartmentId;
    private Boolean isDeleted;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AdditionalAttributes additionalAttributes;
    private Integer statusActivity;
    @ManyToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id", nullable = false)
    private ClsOrganization organization;
    @ManyToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id", nullable = false)
    private ClsDepartment department;
    @ManyToOne
    @JoinColumn(name = "id_subsidy_request_status", referencedColumnName = "id", nullable = false)
    private ClsSubsidyRequestStatus subsidyRequestStatus;
    @ManyToOne
    @JoinColumn(name = "id_processed_user", referencedColumnName = "id")
    private ClsUser processedUser;
    @ManyToOne
    @JoinColumn(name = "id_reassigned_user", referencedColumnName = "id")
    private ClsUser reassignedUser;
    @ManyToOne
    @JoinColumn(name = "id_subsidy", referencedColumnName = "id", nullable = false)
    private ClsSubsidy subsidy;
    @ManyToOne
    @JoinColumn(name = "id_district", referencedColumnName = "id")
    private ClsDistrict district;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "attachment_path", nullable = true, length = -1)
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Basic
    @Column(name = "time_create", nullable = false)
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "time_update", nullable = true)
    public Timestamp getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @Basic
    @Column(name = "time_review", nullable = true)
    public Timestamp getTimeReview() {
        return timeReview;
    }

    public void setTimeReview(Timestamp timeReview) {
        this.timeReview = timeReview;
    }

    @Basic
    @Column(name = "req_basis", nullable = true, length = -1)
    public String getReqBasis() {
        return reqBasis;
    }

    public void setReqBasis(String reqBasis) {
        this.reqBasis = reqBasis;
    }

    @Basic
    @Column(name = "resolution_comment", nullable = true, length = -1)
    public String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
    }

    @Basic
    @Column(name = "old_department_id", nullable = true)
    public Long getOldDepartmentId() {
        return oldDepartmentId;
    }

    public void setOldDepartmentId(Long oldDepartmentId) {
        this.oldDepartmentId = oldDepartmentId;
    }

    //@Basic
    @Column(name = "additional_attributes", nullable = true)
    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Basic
    @Column(name = "status_activity", nullable = true)
    public Integer getStatusActivity() {
        return statusActivity;
    }

    public void setStatusActivity(Integer statusActivity) {
        this.statusActivity = statusActivity;
    }

    @Basic
    @Column(name = "is_deleted")
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
        DocRequestSubsidy that = (DocRequestSubsidy) o;
        return id == that.id && Objects.equals(attachmentPath, that.attachmentPath) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(timeUpdate, that.timeUpdate) && Objects.equals(timeReview, that.timeReview) && Objects.equals(reqBasis, that.reqBasis) && Objects.equals(resolutionComment, that.resolutionComment) && Objects.equals(oldDepartmentId, that.oldDepartmentId) && Objects.equals(additionalAttributes, that.additionalAttributes) && Objects.equals(statusActivity, that.statusActivity) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attachmentPath, timeCreate, timeUpdate, timeReview, reqBasis, resolutionComment, oldDepartmentId, additionalAttributes, statusActivity, isDeleted);
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }


    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }


    public ClsSubsidyRequestStatus getSubsidyRequestStatus() {
        return subsidyRequestStatus;
    }

    public void setSubsidyRequestStatus(ClsSubsidyRequestStatus subsidyRequestStatus) {
        this.subsidyRequestStatus = subsidyRequestStatus;
    }


    public ClsUser getProcessedUser() {
        return processedUser;
    }

    public void setProcessedUser(ClsUser processedUser) {
        this.processedUser = processedUser;
    }

    public ClsUser getReassignedUser() {
        return reassignedUser;
    }

    public void setReassignedUser(ClsUser reassignedUser) {
        this.reassignedUser = reassignedUser;
    }

    public ClsSubsidy getSubsidy() {
        return subsidy;
    }

    public void setSubsidy(ClsSubsidy subsidy) {
        this.subsidy = subsidy;
    }


    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }

    @Basic
    @Column(name = "time_send", nullable = true)
    public Timestamp getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Timestamp timeSend) {
        this.timeSend = timeSend;
    }
}
