package ru.sibdigital.addcovid.model.classifier.gov;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.sibdigital.addcovid.model.Jsonb;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "reg_egrul", schema = "public")
@TypeDefs({
        @TypeDef(name = "JsonbType", typeClass = Jsonb.class)
})
public class RegEgrul {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_egrul_pk")
    @SequenceGenerator(name="seq_reg_egrul_pk", sequenceName = "seq_reg_egrul_pk", allocationSize=1)
    private Long id;
    @Basic
    @Column(name = "load_date", nullable = true)
    private Timestamp loadDate;
    @Basic
    @Column(name = "inn", nullable = true, length = 10)
    private String inn;
    @Basic
    @Column(name = "kpp", nullable = true, length = 9)
    private String kpp;
    @Basic
    @Column(name = "ogrn", nullable = false, length = 13)
    private String ogrn;
    @Basic
    @Column(name = "iogrn")
    private Long iogrn;
    @Basic
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String data;
    @Basic
    @Column(name = "id_migration")
    private Long idMigration;
    @Basic
    @Column(name = "date_actual")
    private Date dateActual;
    @Basic
    @Column(name = "active_status")
    private Integer activeStatus;

    @OneToMany(mappedBy = "regEgrul", fetch = FetchType.LAZY)
    private Set<RegEgrulOkved> regEgrulOkveds;

    @OneToMany(mappedBy = "egrul", fetch = FetchType.LAZY)
    private List<RegFilial> regFilials;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Timestamp loadDate) {
        this.loadDate = loadDate;
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

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public Long getIogrn() {
        return iogrn;
    }

    public void setIogrn(Long iogrn) {
        this.iogrn = iogrn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Set<RegEgrulOkved> getRegEgrulOkveds() {
        return regEgrulOkveds;
    }

    public void setRegEgrulOkveds(Set<RegEgrulOkved> regEgrulOkveds) {
        this.regEgrulOkveds = regEgrulOkveds;
    }

    public Long getIdMigration() {
        return idMigration;
    }

    public void setIdMigration(Long idMigration) {
        this.idMigration = idMigration;
    }

    public Date getDateActual() {
        return dateActual;
    }

    public void setDateActual(Date dateActual) {
        this.dateActual = dateActual;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<RegFilial> getRegFilials() {
        return regFilials;
    }

    public void setRegFilials(List<RegFilial> regFilials) {
        this.regFilials = regFilials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegEgrul regEgrul = (RegEgrul) o;
        return Objects.equals(ogrn, regEgrul.ogrn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ogrn);
    }
}
