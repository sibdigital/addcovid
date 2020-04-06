package ru.sibdigital.addcovid.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "cls_department")
@AllArgsConstructor
@NoArgsConstructor
public class ClsDepartment implements Serializable {

  @Id
  private long id;
  private String name;
  private String description;
  private long statusImport;
  private java.sql.Timestamp timeImport;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

}
