package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_person_count", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegPersonCount {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_PERSON_CNT_GEN", sequenceName = "reg_person_count_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_PERSON_CNT_GEN")
    private Long id;
    private Timestamp timeEdit;
    private Integer personOfficeCnt;
    private Integer personRemoteCnt;


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    private ClsOrganization organization;
    public ClsOrganization getOrganization() {return organization;}
    public void setOrganization(ClsOrganization organization) {this.organization = organization;}

    @OneToOne
    @JoinColumn(name = "id_request", referencedColumnName = "id")
    private DocRequest request;
    public DocRequest getRequest() {return request;}
    public void setRequest(DocRequest request) {this.request = request;}

    @Basic
    @Column(name = "time_edit", nullable = true)
    public Timestamp getEditTime() {
        return timeEdit;
    }
    public void setEditTime(Timestamp timeEdit) {
        this.timeEdit = timeEdit;
    }

    @Basic
    @Column(name = "person_office_cnt")
    public Integer getPersonOfficeCnt() {
        return personOfficeCnt;
    }
    public void setPersonOfficeCnt(Integer personOfficeCnt) {
        this.personOfficeCnt = personOfficeCnt;
    }

    @Basic
    @Column(name = "person_remote_cnt")
    public Integer getPersonRemoteCnt() {
        return personRemoteCnt;
    }
    public void setPersonRemoteCnt(Integer personRemoteCnt) {
        this.personRemoteCnt = personRemoteCnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegPersonCount that = (RegPersonCount) o;
        return Objects.equals(id, that.id) && Objects.equals(organization, that.organization) && Objects.equals(request, that.request) && Objects.equals(timeEdit, that.timeEdit) && Objects.equals(personOfficeCnt, that.personOfficeCnt) && Objects.equals(personRemoteCnt, that.personRemoteCnt);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organization, request, timeEdit, personOfficeCnt, personRemoteCnt);
    }
}
