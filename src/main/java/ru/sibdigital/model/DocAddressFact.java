package ru.sibdigital.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "doc_address_fact")
@AllArgsConstructor
@NoArgsConstructor
public class DocAddressFact implements Serializable {

  private long id;
  private String addressFact;
  private long personOfficeFactCnt;
  private long idRequest;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getAddressFact() {
    return addressFact;
  }

  public void setAddressFact(String addressFact) {
    this.addressFact = addressFact;
  }


  public long getPersonOfficeFactCnt() {
    return personOfficeFactCnt;
  }

  public void setPersonOfficeFactCnt(long personOfficeFactCnt) {
    this.personOfficeFactCnt = personOfficeFactCnt;
  }


  public long getIdRequest() {
    return idRequest;
  }

  public void setIdRequest(long idRequest) {
    this.idRequest = idRequest;
  }

}
