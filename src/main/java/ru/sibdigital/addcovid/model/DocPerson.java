package ru.sibdigital.addcovid.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "doc_person")
@AllArgsConstructor
@NoArgsConstructor
public class DocPerson implements Serializable {
  @Id
  private long id;
  private long idRequest;
  private String lastname;
  private String firstname;
  private String patronymic;
  private String isAgree;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getIdRequest() {
    return idRequest;
  }

  public void setIdRequest(long idRequest) {
    this.idRequest = idRequest;
  }


  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }


  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }


  public String getPatronymic() {
    return patronymic;
  }

  public void setPatronymic(String patronymic) {
    this.patronymic = patronymic;
  }


  public String getIsAgree() {
    return isAgree;
  }

  public void setIsAgree(String isAgree) {
    this.isAgree = isAgree;
  }

}
