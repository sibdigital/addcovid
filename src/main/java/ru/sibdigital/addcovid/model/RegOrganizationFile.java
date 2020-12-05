package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_file", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationFile {
    private int id;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private String attachmentPath;
    private String fileName;
    private String originalFileName;
    private String fileExtension;
    private String hash;
    private Long fileSize;
    private ClsOrganization clsOrganizationByIdOrganization;
    private DocRequest docRequestByIdRequest;

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
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationFile that = (RegOrganizationFile) o;
        return id == that.id &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(attachmentPath, that.attachmentPath) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(originalFileName, that.originalFileName) &&
                Objects.equals(fileExtension, that.fileExtension) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(fileSize, that.fileSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, attachmentPath, fileName, originalFileName, fileExtension, hash, fileSize);
    }

    @ManyToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id", nullable = false)
    public ClsOrganization getClsOrganizationByIdOrganization() {
        return clsOrganizationByIdOrganization;
    }

    public void setClsOrganizationByIdOrganization(ClsOrganization clsOrganizationByIdOrganization) {
        this.clsOrganizationByIdOrganization = clsOrganizationByIdOrganization;
    }

    @ManyToOne
    @JoinColumn(name = "id_request", referencedColumnName = "id")
    public DocRequest getDocRequestByIdRequest() {
        return docRequestByIdRequest;
    }

    public void setDocRequestByIdRequest(DocRequest docRequestByIdRequest) {
        this.docRequestByIdRequest = docRequestByIdRequest;
    }
}
