package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.model.classifier.gov.RegFilial;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_classifier", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationClassifier {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "REG_ORGANIZATION_CLASSIFIER_SEQ_GEN", sequenceName = "REG_ORGANIZATION_CLASSIFIER_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ORGANIZATION_CLASSIFIER_SEQ_GEN")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    private RegEgrul regEgrul;

    @OneToOne
    @JoinColumn(name = "id_egrip", referencedColumnName = "id")
    private RegEgrip regEgrip;

    @OneToOne
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    private RegFilial regFilial;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegEgrul getRegEgrul() {
        return regEgrul;
    }

    public void setRegEgrul(RegEgrul regEgrul) {
        this.regEgrul = regEgrul;
    }

    public RegEgrip getRegEgrip() {
        return regEgrip;
    }

    public void setRegEgrip(RegEgrip regEgrip) {
        this.regEgrip = regEgrip;
    }

    public RegFilial getRegFilial() {
        return regFilial;
    }

    public void setRegFilial(RegFilial regFilial) {
        this.regFilial = regFilial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationClassifier that = (RegOrganizationClassifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
