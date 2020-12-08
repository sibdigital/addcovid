package ru.sibdigital.addcovid.model.classifier.gov;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_egrip_okved", schema = "public")
public class RegEgripOkved {

    private Long id;
    private Long idOkved;
    private Boolean isMain;
    private RegEgrip regEgrip;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "REG_EGRIP_OKVED_SEQ_GEN", sequenceName = "reg_egrip_okved_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_EGRIP_OKVED_SEQ_GEN")
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
        RegEgripOkved that = (RegEgripOkved) o;
        return id == that.id &&
                Objects.equals(idOkved, that.idOkved) &&
                Objects.equals(isMain, that.isMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idOkved, isMain);
    }

    @ManyToOne
    @JoinColumn(name = "id_egrip", referencedColumnName = "id")
    public RegEgrip getRegEgrip() {
        return regEgrip;
    }

    public void setRegEgrip(RegEgrip regEgrip) {
        this.regEgrip = regEgrip;
    }
}
