package ru.sibdigital.addcovid.model;

import lombok.Builder;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Builder(toBuilder = true)
public class RegOrganizationOkvedId implements Serializable {

    @ManyToOne
    @JoinColumn(name="id_organization")
    private ClsOrganization clsOrganization;

    @ManyToOne
    @JoinColumn(name="id_okved")
    private Okved okved;

    public RegOrganizationOkvedId() {

    }

    public RegOrganizationOkvedId(ClsOrganization clsOrganization, Okved okved) {
        this.clsOrganization = clsOrganization;
        this.okved = okved;
    }

    public ClsOrganization getClsOrganization() {
        return clsOrganization;
    }

    public void setClsOrganization(ClsOrganization clsOrganization) {
        this.clsOrganization = clsOrganization;
    }

    public Okved getOkved() {
        return okved;
    }

    public void setOkved(Okved okved) {
        this.okved = okved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationOkvedId that = (RegOrganizationOkvedId) o;
        return Objects.equals(clsOrganization, that.clsOrganization) &&
                Objects.equals(okved, that.okved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clsOrganization, okved);
    }
}
