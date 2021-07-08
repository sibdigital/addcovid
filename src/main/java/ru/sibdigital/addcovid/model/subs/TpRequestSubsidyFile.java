package ru.sibdigital.addcovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsUser;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tp_request_subsidy_file", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TpRequestSubsidyFile {
    private int id;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private String attachmentPath;
    private String fileName;
    private String originalFileName;
    private String fileExtension;
    private String hash;
    private Integer fileSize;
    private Boolean isSignature;
    private DocRequestSubsidy requestSubsidy;
    private ClsOrganization organization;
    private ClsDepartment department;
    private ClsUser processedUser;
    private TpRequestSubsidyFile requestSubsidyFile;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    @Column(name = "attachment_path")
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Basic
    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "original_file_name")
    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Basic
    @Column(name = "file_extension")
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Basic
    @Column(name = "hash")
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Basic
    @Column(name = "file_size")
    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @Basic
    @Column(name = "is_signature")
    public Boolean getSignature() {
        return isSignature;
    }

    public void setSignature(Boolean signature) {
        isSignature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpRequestSubsidyFile that = (TpRequestSubsidyFile) o;
        return id == that.id && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(attachmentPath, that.attachmentPath) && Objects.equals(fileName, that.fileName) && Objects.equals(originalFileName, that.originalFileName) && Objects.equals(fileExtension, that.fileExtension) && Objects.equals(hash, that.hash) && Objects.equals(fileSize, that.fileSize) && Objects.equals(isSignature, that.isSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, attachmentPath, fileName, originalFileName, fileExtension, hash, fileSize, isSignature);
    }

    @ManyToOne
    @JoinColumn(name = "id_request", referencedColumnName = "id", nullable = false)
    public DocRequestSubsidy getRequestSubsidy() {
        return requestSubsidy;
    }

    public void setRequestSubsidy(DocRequestSubsidy requestSubsidy) {
        this.requestSubsidy = requestSubsidy;
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
    @JoinColumn(name = "id_processed_user", referencedColumnName = "id")
    public ClsUser getProcessedUser() {
        return processedUser;
    }

    public void setProcessedUser(ClsUser processedUser) {
        this.processedUser = processedUser;
    }

    @ManyToOne
    @JoinColumn(name = "id_subsidy_request_file", referencedColumnName = "id")
    public TpRequestSubsidyFile getRequestSubsidyFile() {
        return requestSubsidyFile;
    }

    public void setRequestSubsidyFile(TpRequestSubsidyFile requestSubsidyFile) {
        this.requestSubsidyFile = requestSubsidyFile;
    }
}
