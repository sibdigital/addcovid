package ru.sibdigital.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "cls_organization")
@AllArgsConstructor
@NoArgsConstructor
public class ClsOrganization implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String shortName;
    private String inn;
    private String ogrn;
    private String addressJur;
    private String okvedAdd;
    private String okved;
    private String email;
    private String phone;
    private Long statusImport;
    private Timestamp timeImport;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }


    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }


    public String getAddressJur() {
        return addressJur;
    }

    public void setAddressJur(String addressJur) {
        this.addressJur = addressJur;
    }


    public String getOkvedAdd() {
        return okvedAdd;
    }

    public void setOkvedAdd(String okvedAdd) {
        this.okvedAdd = okvedAdd;
    }


    public String getOkved() {
        return okved;
    }

    public void setOkved(String okved) {
        this.okved = okved;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

}
