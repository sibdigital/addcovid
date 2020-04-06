package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_organization", schema = "public", catalog = "addcovid")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsOrganization {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "CLS_ORG_GEN", sequenceName = "cls_organization_id_seq")
    private Long id;
    private String name;
    private String shortName;
    private String inn;
    private String ogrn;
    private String hashCode;
    private String addressJur;
    private String okvedAdd;
    private String okved;
    private String email;
    private String phone;
    private Integer statusImport;
    private Timestamp timeImport;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "short_name", nullable = false, length = 255)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Basic
    @Column(name = "inn", nullable = false, length = 12)
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    @Basic
    @Column(name = "ogrn", nullable = false, length = 13)
    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    @Basic
    @Column(name = "hash_code", nullable = false, length = -1)
    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    @Basic
    @Column(name = "address_jur", nullable = false, length = 255)
    public String getAddressJur() {
        return addressJur;
    }

    public void setAddressJur(String addressJur) {
        this.addressJur = addressJur;
    }

    @Basic
    @Column(name = "okved_add", nullable = true)
    public String getOkvedAdd() {
        return okvedAdd;
    }

    public void setOkvedAdd(String okvedAdd) {
        this.okvedAdd = okvedAdd;
    }

    @Basic
    @Column(name = "okved", nullable = false, length = -1)
    public String getOkved() {
        return okved;
    }

    public void setOkved(String okved) {
        this.okved = okved;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "phone", nullable = false, length = 100)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "status_import", nullable = false)
    public Integer getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(Integer statusImport) {
        this.statusImport = statusImport;
    }

    @Basic
    @Column(name = "time_import", nullable = true)
    public Timestamp getTimeImport() {
        return timeImport;
    }

    public void setTimeImport(Timestamp timeImport) {
        this.timeImport = timeImport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsOrganization that = (ClsOrganization) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(inn, that.inn) &&
                Objects.equals(ogrn, that.ogrn) &&
                Objects.equals(hashCode, that.hashCode) &&
                Objects.equals(addressJur, that.addressJur) &&
                Objects.equals(okvedAdd, that.okvedAdd) &&
                Objects.equals(okved, that.okved) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(statusImport, that.statusImport) &&
                Objects.equals(timeImport, that.timeImport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shortName, inn, ogrn, hashCode, addressJur, okvedAdd, okved, email, phone, statusImport, timeImport);
    }
}
