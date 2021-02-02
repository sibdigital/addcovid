package ru.sibdigital.addcovid.model.classifier.gov;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.sibdigital.addcovid.model.Jsonb;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_filial", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
        @TypeDef(name = "JsonbType", typeClass = Jsonb.class)
})
public class RegFilial {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_FILIAL_GEN", sequenceName = "reg_filial_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_FILIAL_GEN")
    private Long id;
    @Basic
    @Column(name = "inn", nullable = true, length = 10)
    private String inn;
    @Basic
    @Column(name = "kpp", nullable = true, length = 9)
    private String kpp;
    @Basic
    @Column(name = "full_name")
    private String fullName;
    @Basic
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String data;
    @ManyToOne
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    private RegEgrul egrul;
    @Basic
    @Column(name = "type")
    private Integer type;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "kladr_code")
    private String kladrCode;
    @Basic
    @Column(name = "active_status")
    private Integer activeStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public RegEgrul getEgrul() {
        return egrul;
    }
    public void setEgrul(RegEgrul egrul) {
        this.egrul = egrul;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKladrCode() {
        return kladrCode;
    }

    public void setKladrCode(String kladrCode) {
        this.kladrCode = kladrCode;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }
    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegFilial regFilial = (RegFilial) o;
        return Objects.equals(id, regFilial.id) &&
                Objects.equals(inn, regFilial.inn) &&
                Objects.equals(kpp, regFilial.kpp) &&
                Objects.equals(fullName, regFilial.fullName) &&
                Objects.equals(data, regFilial.data) &&
                Objects.equals(egrul, regFilial.egrul) &&
                Objects.equals(type, regFilial.type) &&
                Objects.equals(address, regFilial.address) &&
                Objects.equals(kladrCode, regFilial.kladrCode) &&
                Objects.equals(activeStatus, regFilial.activeStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inn, kpp, fullName, data, egrul, type, address, kladrCode, activeStatus);
    }
}
