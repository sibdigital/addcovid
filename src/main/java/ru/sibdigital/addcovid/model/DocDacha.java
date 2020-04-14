package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "doc_dacha", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class DocDacha {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_DACHA_SEQ_GEN", sequenceName = "doc_dacha_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_DACHA_SEQ_GEN")
    private Integer id;
    private String district;
    private String address;
    private LocalDate validDate;
    private String link;
    private String raion;
    private String naspunkt;
    private Boolean isAgree;
    private Boolean isProtect;
    private Timestamp timeCreate;
    private Integer statusImport;
    private Timestamp timeImport;
    private Integer statusReview;
    private Timestamp timeReview;
    private String rejectComment;
    private String phone;
    private String email;

    @OneToMany(targetEntity = DocDachaPerson.class, mappedBy = "docDachaByIdDocDacha", fetch = FetchType.LAZY)
    private Collection<DocDachaPerson> docDachaPersons;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "district")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "valid_date")
    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    @Basic
    @Column(name = "link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Basic
    @Column(name = "raion")
    public String getRaion() {
        return raion;
    }

    public void setRaion(String raion) {
        this.raion = raion;
    }

    @Basic
    @Column(name = "naspunkt")
    public String getNaspunkt() {
        return naspunkt;
    }

    public void setNaspunkt(String naspunkt) {
        this.naspunkt = naspunkt;
    }

    @Basic
    @Column(name = "is_agree")
    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }

    @Basic
    @Column(name = "is_protect")
    public Boolean getProtect() {
        return isProtect;
    }

    public void setProtect(Boolean protect) {
        isProtect = protect;
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
    @Column(name = "status_import")
    public Integer getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(Integer statusImport) {
        this.statusImport = statusImport;
    }

    @Basic
    @Column(name = "time_import")
    public Timestamp getTimeImport() {
        return timeImport;
    }

    public void setTimeImport(Timestamp timeImport) {
        this.timeImport = timeImport;
    }

    @Basic
    @Column(name = "status_review")
    public Integer getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(Integer statusReview) {
        this.statusReview = statusReview;
    }

    @Basic
    @Column(name = "time_review")
    public Timestamp getTimeReview() {
        return timeReview;
    }

    public void setTimeReview(Timestamp timeReview) {
        this.timeReview = timeReview;
    }

    @Basic
    @Column(name = "reject_comment")
    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocDacha docDacha = (DocDacha) o;
        return id == docDacha.id &&
                Objects.equals(district, docDacha.district) &&
                Objects.equals(address, docDacha.address) &&
                Objects.equals(validDate, docDacha.validDate) &&
                Objects.equals(link, docDacha.link) &&
                Objects.equals(raion, docDacha.raion) &&
                Objects.equals(naspunkt, docDacha.naspunkt) &&
                Objects.equals(isAgree, docDacha.isAgree) &&
                Objects.equals(isProtect, docDacha.isProtect) &&
                Objects.equals(timeCreate, docDacha.timeCreate) &&
                Objects.equals(statusImport, docDacha.statusImport) &&
                Objects.equals(timeImport, docDacha.timeImport) &&
                Objects.equals(statusReview, docDacha.statusReview) &&
                Objects.equals(timeReview, docDacha.timeReview) &&
                Objects.equals(rejectComment, docDacha.rejectComment) &&
                Objects.equals(phone, docDacha.phone) &&
                Objects.equals(email, docDacha.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, district, address, validDate, link, raion, naspunkt, isAgree, isProtect, timeCreate, statusImport, timeImport, statusReview, timeReview, rejectComment, phone, email);
    }

    public Collection<DocDachaPerson> getDocDachaPersons() {
        return docDachaPersons;
    }

    public void setDocDachaPersons(Collection<DocDachaPerson> docDachaPersons) {
        this.docDachaPersons = docDachaPersons;
    }
}
