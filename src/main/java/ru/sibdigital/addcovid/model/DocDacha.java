package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "doc_dacha", schema = "public", catalog = "addcovid")
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
    @SequenceGenerator(name = "DOC_SEQ_GEN", sequenceName = "doc_dacha_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_SEQ_GEN")
    private int id;
    private String lastname;
    private String firstname;
    private String patronymic;
    private Integer age;
    private Boolean isAgree;
    private Boolean isProtect;
    private Timestamp timeCreate;
    private Integer statusImport;
    private Timestamp timeImport;
    private Integer statusReview;
    private Timestamp timeReview;
    private String rejectComment;

    @OneToMany(targetEntity = DocDachaAddr.class, mappedBy="docDachaByIdDocDacha", fetch = FetchType.EAGER)
    private Collection<DocDachaAddr> docDachaAddrs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Basic
    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocDacha that = (DocDacha) o;
        return id == that.id &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(patronymic, that.patronymic) &&
                Objects.equals(age, that.age) &&
                Objects.equals(isAgree, that.isAgree) &&
                Objects.equals(isProtect, that.isProtect) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(statusImport, that.statusImport) &&
                Objects.equals(timeImport, that.timeImport) &&
                Objects.equals(statusReview, that.statusReview) &&
                Objects.equals(timeReview, that.timeReview) &&
                Objects.equals(rejectComment, that.rejectComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastname, firstname, patronymic, age, isAgree, isProtect, timeCreate, statusImport, timeImport, statusReview, timeReview, rejectComment);
    }

    public Collection<DocDachaAddr> getDocDachaAddrs() {
        return docDachaAddrs;
    }

    public void setDocDachaAddrs(Collection<DocDachaAddr> docDachaAddrsById) {
        this.docDachaAddrs = docDachaAddrsById;
    }
}
