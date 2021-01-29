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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_egrul_pk")
    @SequenceGenerator(name="seq_reg_filial_pk", sequenceName = "seq_reg_egrul_pk", allocationSize=1)
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
    @Column(name = "kladr_address")
    private String kladrAddress;
    @Basic
    @Column(name = "kladr_address_hash")
    private Integer kladrAddressHash;
    @Basic
    @Column(name = "kladr_code")
    private String kladrCode;


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

    public String getKladrAddress() {
        return kladrAddress;
    }

    public void setKladrAddress(String kladrAddress) {
        this.kladrAddress = kladrAddress;
    }

    public Integer getKladrAddressHash() {
        return kladrAddressHash;
    }

    public void setKladrAddressHash(Integer kladrAddressHash) {
        this.kladrAddressHash = kladrAddressHash;
    }

    public String getKladrCode() {
        return kladrCode;
    }

    public void setKladrCode(String kladrCode) {
        this.kladrCode = kladrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegFilial regFilial = (RegFilial) o;
        return Objects.equals(id, regFilial.id) &&
                Objects.equals(egrul, regFilial.egrul) &&
                Objects.equals(type, regFilial.type) &&
                Objects.equals(kladrAddressHash, regFilial.kladrAddressHash) ;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, egrul, type, kladrAddressHash);
    }
}
