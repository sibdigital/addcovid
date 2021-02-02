package ru.sibdigital.addcovid.model.classifier.gov;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.sibdigital.addcovid.model.Jsonb;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "reg_egrip", schema = "public")
@TypeDef(name = "Jsonb", typeClass = Jsonb.class)
public class RegEgrip {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_egrip_pk")
    @SequenceGenerator(name="seq_reg_egrip_pk", sequenceName = "seq_reg_egrip_pk", allocationSize=1)
    private Long id;
    @Basic
    @Column(name = "load_date", nullable = true)
    private Timestamp loadDate;
    @Basic
    @Column(name = "inn", nullable = true, length = 12)
    private String inn;
    @Basic
    @Column(name = "ogrn", nullable = false, length = 15)
    private String ogrn;
    @Basic
    @Column(name = "iogrn")
    private Long iogrn;
    @Basic
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "Jsonb")
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
    @Basic
    @Column(name = "type_egrip")
    private Short typeEgrip;

    @OneToMany(mappedBy = "regEgrip", fetch = FetchType.LAZY)
    private Set<RegEgripOkved> regEgripOkveds;

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

    public Set<RegEgripOkved> getRegEgripOkveds() {
        return regEgripOkveds;
    }

    public void setRegEgripOkveds(Set<RegEgripOkved> regEgripOkveds) {
        this.regEgripOkveds = regEgripOkveds;
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

    public Short getTypeEgrip() {
        return typeEgrip;
    }

    public void setTypeEgrip(Short typeEgrip) {
        this.typeEgrip = typeEgrip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegEgrip regEgrip = (RegEgrip) o;
        return Objects.equals(ogrn, regEgrip.ogrn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ogrn);
    }
}
