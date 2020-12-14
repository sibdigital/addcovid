package ru.sibdigital.addcovid.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reg_type_request_restriction_type", schema = "public")
public class RegTypeRequestRestrictionType {

    @EmbeddedId
    private RegTypeRequestRestrictionTypeId regTypeRequestRestrictionTypeId;

    public RegTypeRequestRestrictionType() {
    }

    public RegTypeRequestRestrictionType(RegTypeRequestRestrictionTypeId regTypeRequestRestrictionTypeId) {
        this.regTypeRequestRestrictionTypeId = regTypeRequestRestrictionTypeId;
    }

    public RegTypeRequestRestrictionTypeId getRegTypeRequestRestrictionTypeId() {
        return regTypeRequestRestrictionTypeId;
    }

    public void setRegTypeRequestRestrictionTypeId(RegTypeRequestRestrictionTypeId regTypeRequestRestrictionTypeId) {
        this.regTypeRequestRestrictionTypeId = regTypeRequestRestrictionTypeId;
    }
}
