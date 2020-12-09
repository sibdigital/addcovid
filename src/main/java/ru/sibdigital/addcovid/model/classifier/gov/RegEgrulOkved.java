package ru.sibdigital.addcovid.model.classifier.gov;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_egrul_okved", schema = "public")
public class RegEgrulOkved {

    private Long id;
    private Long idOkved;
    private Boolean isMain;
    private RegEgrul regEgrul;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "REG_EGRUL_OKVED_SEQ_GEN", sequenceName = "reg_egrul_okved_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_EGRUL_OKVED_SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "id_okved")
    public Long getIdOkved() {
        return idOkved;
    }

    public void setIdOkved(Long idOkved) {
        this.idOkved = idOkved;
    }

    @Basic
    @Column(name = "is_main")
    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegEgrulOkved that = (RegEgrulOkved) o;
        return id == that.id &&
                Objects.equals(idOkved, that.idOkved) &&
                Objects.equals(isMain, that.isMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idOkved, isMain);
    }

    @ManyToOne
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    public RegEgrul getRegEgrul() {
        return regEgrul;
    }

    public void setRegEgrul(RegEgrul regEgrul) {
        this.regEgrul = regEgrul;
    }
}
