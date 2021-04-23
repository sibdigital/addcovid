package ru.sibdigital.addcovid.model.egr;

public enum EgripTypes {
    INDIVIDUAL_ENTREPRENEUR(Short.valueOf("1")),
    HEAD_OF_KFH(Short.valueOf("2"));

    private final Short value;
    private EgripTypes(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
