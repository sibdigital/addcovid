package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_address_fact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationAddressFact {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_ORG_ADDR_FACT_SEQ_GEN", sequenceName = "reg_organization_address_fact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ORG_ADDR_FACT_SEQ_GEN")
    private Long id;
    private Integer organizationId;
    private boolean isDeleted = false;
    private Timestamp timeCreate;
    private String fiasObjectGuid;
    private String fiasRegionGuid;
    private String fiasRaionGuid;
    private String fullAddress;
    private boolean isHand = false;

    @JoinColumn(name="id_request", referencedColumnName = "id", nullable = true)
    @OneToOne
    @JsonIgnore
    private DocRequest docRequestAddressFact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
    @Column(name = "fias_objectguid")
    public String getFiasObjectGuid() {
        return fiasObjectGuid;
    }

    public void setFiasObjectGuid(String fiasObjectGuid) {
        this.fiasObjectGuid = fiasObjectGuid;
    }

    @Basic
    @Column(name = "fias_region_objectguid")
    public String getFiasRegionGuid() {
        return fiasRegionGuid;
    }

    public void setFiasRegionGuid(String fiasRegionGuid) {
        this.fiasRegionGuid = fiasRegionGuid;
    }

    @Basic
    @Column(name = "fias_raion_objectguid")
    public String getFiasRaionGuid() {
        return fiasRaionGuid;
    }

    public void setFiasRaionGuid(String fiasRaionGuid) {
        this.fiasRaionGuid = fiasRaionGuid;
    }

    @Basic
    @Column(name = "full_address")
    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Basic
    @Column(name = "is_hand")
    public boolean isHand() {
        return isHand;
    }

    public void setHand(boolean hand) {
        isHand = hand;
    }

    @Basic
    @Column(name = "id_organization")
    public Integer getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @JsonIgnore
    public DocRequest getDocRequestAddressFact() {
        return docRequestAddressFact;
    }
    public void setDocRequestAddressFact(DocRequest docRequestAddressFact) {
        this.docRequestAddressFact = docRequestAddressFact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationAddressFact that = (RegOrganizationAddressFact) o;
        return Objects.equals(id, that.id) &&
                //Objects.equals(organization.getId(), that.organization.getId()) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(docRequestAddressFact.getId(), that.docRequestAddressFact.getId()) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(fiasObjectGuid, that.fiasObjectGuid) &&
                Objects.equals(fiasRaionGuid, that.fiasRaionGuid) &&
                Objects.equals(fiasRegionGuid, that.fiasRegionGuid);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, isHand, timeCreate, fiasObjectGuid, fiasRegionGuid, fiasRaionGuid, fullAddress);
    }


}
