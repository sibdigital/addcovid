package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_organization_contact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsOrganizationContact {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_ORGANIZATION_CONTACT_SEQ_GEN", sequenceName = "cls_organization_contact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_ORGANIZATION_CONTACT_SEQ_GEN")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    @JsonIgnore
    private ClsOrganization organization;

    @Basic
    @Column(name = "type")
    private Integer type;

    @Basic
    @Column(name = "contact_value", nullable = true)
    private String contactValue;

    @Basic
    @Column(name = "contact_person", nullable = true)
    private String contactPerson;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Integer getType() { return type; }

    public void setType(Integer type) { this.type = type; }

    public String getContactValue() { return contactValue; }

    public void setContactValue(String contactValue) { this.contactValue = contactValue; }

    public String getContactPerson() { return contactPerson; }

    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public ClsOrganization getOrganization() { return organization; }

    public void setOrganization(ClsOrganization organization) { this.organization = organization; }
}
