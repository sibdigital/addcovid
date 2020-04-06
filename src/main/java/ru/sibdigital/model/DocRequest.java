package ru.sibdigital.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;


@Entity
@Table(name = "doc_request")
@AllArgsConstructor
@NoArgsConstructor
public class DocRequest {

    private Long id;
    private Long personOfficeCnt;
    private Long personRemoteCnt;
    private Long personSlrySaveCnt;
    private Long personOfficeFactCnt;
    private Long idOrganization;
    private Long idDepartment;
    private String attachmentPath;
    private Long statusReview;
    private Timestamp timeCreate;
    private Long statusImport;
    private Timestamp timeImport;
    private Timestamp timeReview;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getPersonOfficeCnt() {
        return personOfficeCnt;
    }

    public void setPersonOfficeCnt(Long personOfficeCnt) {
        this.personOfficeCnt = personOfficeCnt;
    }


    public Long getPersonRemoteCnt() {
        return personRemoteCnt;
    }

    public void setPersonRemoteCnt(Long personRemoteCnt) {
        this.personRemoteCnt = personRemoteCnt;
    }


    public Long getPersonSlrySaveCnt() {
        return personSlrySaveCnt;
    }

    public void setPersonSlrySaveCnt(Long personSlrySaveCnt) {
        this.personSlrySaveCnt = personSlrySaveCnt;
    }


    public Long getPersonOfficeFactCnt() {
        return personOfficeFactCnt;
    }

    public void setPersonOfficeFactCnt(Long personOfficeFactCnt) {
        this.personOfficeFactCnt = personOfficeFactCnt;
    }


    public Long getIdOrganization() {
        return idOrganization;
    }

    public void setIdOrganization(Long idOrganization) {
        this.idOrganization = idOrganization;
    }


    public Long getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }


    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }


    public Long getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(Long statusReview) {
        this.statusReview = statusReview;
    }


    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }


    public Long getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(Long statusImport) {
        this.statusImport = statusImport;
    }


    public Timestamp getTimeImport() {
        return timeImport;
    }

    public void setTimeImport(Timestamp timeImport) {
        this.timeImport = timeImport;
    }


    public Timestamp getTimeReview() {
        return timeReview;
    }

    public void setTimeReview(Timestamp timeReview) {
        this.timeReview = timeReview;
    }

}
