package ru.sibdigital.addcovid.model.classifier.gov;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.sibdigital.addcovid.model.Jsonb;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

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
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String data;
    @Basic
    @Column(name = "id_migration")
    private Long idMigration;


//    @OneToMany(mappedBy = "regEgrulOkvedId.regEgrul")
//    private Set<RegEgrulOkved> regEgrulOkveds;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


//    public Set<RegEgrulOkved> getRegEgrulOkveds() {
//        return regEgrulOkveds;
//    }
//
//    public void setRegEgrulOkveds(Set<RegEgrulOkved> regEgrulOkveds) {
//        this.regEgrulOkveds = regEgrulOkveds;
//    }

    public Long getIdMigration() {
        return idMigration;
    }

    public void setIdMigration(Long idMigration) {
        this.idMigration = idMigration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegEgrul regEgrul = (RegEgrul) o;
        return Objects.equals(inn, regEgrul.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inn);
    }
}
