package ru.sibdigital.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "doc_request")
@AllArgsConstructor
@NoArgsConstructor
public class DocRequest {

  private long id;
  private long personOfficeCnt;
  private long personRemoteCnt;
  private long personSlrySaveCnt;
  private long personOfficeFactCnt;
  private long idOrganization;
  private long idDepartment;
  private String attachmentPath;
  private long statusReview;
  private java.sql.Timestamp timeCreate;
  private long statusImport;
  private java.sql.Timestamp timeImport;
  private java.sql.Timestamp timeReview;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getPersonOfficeCnt() {
    return personOfficeCnt;
  }

  public void setPersonOfficeCnt(long personOfficeCnt) {
    this.personOfficeCnt = personOfficeCnt;
  }


  public long getPersonRemoteCnt() {
    return personRemoteCnt;
  }

  public void setPersonRemoteCnt(long personRemoteCnt) {
    this.personRemoteCnt = personRemoteCnt;
  }


  public long getPersonSlrySaveCnt() {
    return personSlrySaveCnt;
  }

  public void setPersonSlrySaveCnt(long personSlrySaveCnt) {
    this.personSlrySaveCnt = personSlrySaveCnt;
  }


  public long getPersonOfficeFactCnt() {
    return personOfficeFactCnt;
  }

  public void setPersonOfficeFactCnt(long personOfficeFactCnt) {
    this.personOfficeFactCnt = personOfficeFactCnt;
  }


  public long getIdOrganization() {
    return idOrganization;
  }

  public void setIdOrganization(long idOrganization) {
    this.idOrganization = idOrganization;
  }


  public long getIdDepartment() {
    return idDepartment;
  }

  public void setIdDepartment(long idDepartment) {
    this.idDepartment = idDepartment;
  }


  public String getAttachmentPath() {
    return attachmentPath;
  }

  public void setAttachmentPath(String attachmentPath) {
    this.attachmentPath = attachmentPath;
  }


  public long getStatusReview() {
    return statusReview;
  }

  public void setStatusReview(long statusReview) {
    this.statusReview = statusReview;
  }


  public java.sql.Timestamp getTimeCreate() {
    return timeCreate;
  }

  public void setTimeCreate(java.sql.Timestamp timeCreate) {
    this.timeCreate = timeCreate;
  }


  public long getStatusImport() {
    return statusImport;
  }

  public void setStatusImport(long statusImport) {
    this.statusImport = statusImport;
  }


  public java.sql.Timestamp getTimeImport() {
    return timeImport;
  }

  public void setTimeImport(java.sql.Timestamp timeImport) {
    this.timeImport = timeImport;
  }


  public java.sql.Timestamp getTimeReview() {
    return timeReview;
  }

  public void setTimeReview(java.sql.Timestamp timeReview) {
    this.timeReview = timeReview;
  }

}
