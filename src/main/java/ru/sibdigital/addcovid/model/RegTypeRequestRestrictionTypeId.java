package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RegTypeRequestRestrictionTypeId implements Serializable {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_type_request")
    private ClsTypeRequest clsTypeRequest;

    @ManyToOne
    @JoinColumn(name="id_restriction_type")
    private ClsRestrictionType clsRestrictionType;

    public RegTypeRequestRestrictionTypeId() {
    }

    public RegTypeRequestRestrictionTypeId(ClsTypeRequest clsTypeRequest, ClsRestrictionType clsRestrictionType) {
        this.clsTypeRequest = clsTypeRequest;
        this.clsRestrictionType = clsRestrictionType;
    }

    public ClsTypeRequest getClsTypeRequest() {
        return clsTypeRequest;
    }

    public void setClsTypeRequest(ClsTypeRequest clsTypeRequest) {
        this.clsTypeRequest = clsTypeRequest;
    }

    public ClsRestrictionType getClsRestrictionType() {
        return clsRestrictionType;
    }

    public void setClsRestrictionType(ClsRestrictionType clsRestrictionType) {
        this.clsRestrictionType = clsRestrictionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegTypeRequestRestrictionTypeId that = (RegTypeRequestRestrictionTypeId) o;
        return Objects.equals(clsTypeRequest, that.clsTypeRequest) &&
                Objects.equals(clsRestrictionType, that.clsRestrictionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clsTypeRequest, clsRestrictionType);
    }
}
