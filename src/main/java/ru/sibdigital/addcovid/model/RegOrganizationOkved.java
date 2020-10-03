package ru.sibdigital.addcovid.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reg_organization_okved", schema = "public")
public class RegOrganizationOkved {

    @EmbeddedId
    private RegOrganizationOkvedId regOrganizationOkvedId;
    @Column(name = "is_main")
    private Boolean isMain;

    public RegOrganizationOkved() {
    }

    public RegOrganizationOkved(RegOrganizationOkvedId regOrganizationOkvedId, Boolean isMain) {
        this.regOrganizationOkvedId = regOrganizationOkvedId;
        this.isMain = isMain;
    }

    public RegOrganizationOkvedId getRegOrganizationOkvedId() {
        return regOrganizationOkvedId;
    }

    public void setRegOrganizationOkvedId(RegOrganizationOkvedId regOrganizationOkvedId) {
        this.regOrganizationOkvedId = regOrganizationOkvedId;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }
}
