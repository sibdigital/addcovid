package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_address_fact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationAddressFact {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_ORGANIZATION_ADDRESS_FACT_SEQ_GEN", sequenceName = "reg_organization_address_fact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ORGANIZATION_ADDRESS_FACT_SEQ_GEN")
    private Long id;

    @Basic
    @Column(name = "fullAddress")
    private String fullAddress;

    @Basic
    @Column(name = "isDeleted")
    private boolean isDeleted = false;

    @Basic
    @Column(name = "timeCreate")
    private Date timeCreate;

    @Basic
    @Column(name = "fiasRegionObjectGuid")
    private String fiasRegionObjectGuid;

    @Basic
    @Column(name = "fiasRaionObjectGuid")
    private String fiasRaionObjectGuid;


    @Basic
    @Column(name = "isHand")
    private boolean isHand = false;

//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name="id_request", nullable=false)
//    private DocRequest docRequestAddressFact;

    @OneToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    private ClsOrganization organization;

    @OneToOne
    @JoinColumn(name="id_request", referencedColumnName = "id")
    @JsonIgnore
    private DocRequest docRequest;

//    public Long getOrganizationId() {
//        return organization.getId();
//    }
//
//    public void setOrganizationId(Integer organizationId) {
//        this.organizationId = organizationId;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getFiasRegionObjectGuid() {
        return fiasRegionObjectGuid;
    }

    public void setFiasRegionObjectGuid(String fiasRegionObjectGuid) {
        this.fiasRegionObjectGuid = fiasRegionObjectGuid;
    }

    public String getFiasRaionObjectGuid() {
        return fiasRaionObjectGuid;
    }

    public void setFiasRaionObjectGuid(String fiasRaionObjectGuid) {
        this.fiasRaionObjectGuid = fiasRaionObjectGuid;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean getIsHand() {
        return isHand;
    }

    public void setFullAddress(boolean isHand) {
        this.isHand = isHand;
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    public DocRequest getDocRequest() {
        return docRequest;
    }

    public void setDocRequest(DocRequest docRequest) {
        this.docRequest = docRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationAddressFact that = (RegOrganizationAddressFact) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(fiasRegionObjectGuid, that.fiasRegionObjectGuid) &&
                Objects.equals(fiasRaionObjectGuid, that.fiasRaionObjectGuid) &&
                Objects.equals(fullAddress, that.fullAddress) &&
                Objects.equals(isHand, that.isHand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, fiasRegionObjectGuid, fiasRaionObjectGuid, fullAddress, isHand);
    }


}
