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
    private Long fiasRegionObjectId;
    private Long fiasRaionObjectId;
    private Long fiasCityObjectId;
    private Long fiasStreetObjectId;
    private Long fiasHouseObjectId;
    private Long fiasObjectId;
    private String fullAddress;
    private String streetHand;
    private String houseHand;
    private String apartmentHand;
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
    @Column(name = "fias_objectid")
    public Long getFiasObjectId() {
        return fiasObjectId;
    }

    public void setFiasObjectId(Long fiasObjectGuid) {
        this.fiasObjectId = fiasObjectGuid;
    }

    @Basic
    @Column(name = "fias_region_objectid")
    public Long getFiasRegionObjectId() {
        return fiasRegionObjectId;
    }

    public void setFiasRegionObjectId(Long fiasRegionObjectId) {
        this.fiasRegionObjectId = fiasRegionObjectId;
    }

    @Basic
    @Column(name = "fias_raion_objectid")
    public Long getFiasRaionObjectId() {
        return fiasRaionObjectId;
    }

    public void setFiasRaionObjectId(Long fiasRaionObjectId) {
        this.fiasRaionObjectId = fiasRaionObjectId;
    }

    @Basic
    @Column(name = "fias_city_objectid")
    public Long getFiasCityObjectId() {
        return fiasCityObjectId;
    }

    public void setFiasCityObjectId(Long fiasCityObjectId) {
        this.fiasCityObjectId = fiasCityObjectId;
    }

    @Basic
    @Column(name = "fias_street_objectid")
    public Long getFiasStreetObjectId() {
        return fiasStreetObjectId;
    }

    public void setFiasStreetObjectId(Long fiasStreetObjectId) {
        this.fiasStreetObjectId = fiasStreetObjectId;
    }

    @Basic
    @Column(name = "fias_house_objectid")
    public Long getFiasHouseObjectId() {
        return fiasHouseObjectId;
    }

    public void setFiasHouseObjectId(Long fiasHouseObjectId) {
        this.fiasHouseObjectId = fiasHouseObjectId;
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
    @Column(name = "street_hand")
    public String getStreetHand() {
        return streetHand;
    }

    public void setStreetHand(String streetHand) {
        this.streetHand = streetHand;
    }

    @Basic
    @Column(name = "house_hand")
    public String getHouseHand() {
        return houseHand;
    }

    public void setHouseHand(String houseHand) {
        this.houseHand = houseHand;
    }

    @Basic
    @Column(name = "apartment_hand")
    public String getApartmentHand() {
        return apartmentHand;
    }

    public void setApartmentHand(String apartmentHand) {
        this.apartmentHand = apartmentHand;
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
                Objects.equals(fiasRegionObjectId, that.fiasRegionObjectId) &&
                Objects.equals(fiasRaionObjectId, that.fiasRaionObjectId) &&
                Objects.equals(fiasCityObjectId, that.fiasCityObjectId) &&
                Objects.equals(fiasStreetObjectId, that.fiasStreetObjectId) &&
                Objects.equals(fiasObjectId, that.fiasObjectId) &&
                Objects.equals(houseHand, that.houseHand) &&
                Objects.equals(streetHand, that.streetHand) &&
                Objects.equals(apartmentHand, that.apartmentHand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, isHand, timeCreate, fiasRegionObjectId, fiasRaionObjectId, fiasCityObjectId, fiasStreetObjectId, fiasObjectId, houseHand, streetHand, apartmentHand);
    }


}
