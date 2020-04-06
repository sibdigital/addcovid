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

  private Long id;
  private String addressFact;
  private Long personOfficeFactCnt;
  private Long idRequest;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getAddressFact() {
    return addressFact;
  }

  public void setAddressFact(String addressFact) {
    this.addressFact = addressFact;
  }


  public Long getPersonOfficeFactCnt() {
    return personOfficeFactCnt;
  }

  public void setPersonOfficeFactCnt(Long personOfficeFactCnt) {
    this.personOfficeFactCnt = personOfficeFactCnt;
  }


  public Long getIdRequest() {
    return idRequest;
  }

  public void setIdRequest(Long idRequest) {
    this.idRequest = idRequest;
  }

}
