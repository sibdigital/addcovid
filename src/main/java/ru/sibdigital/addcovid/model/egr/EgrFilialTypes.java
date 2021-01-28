package ru.sibdigital.addcovid.model.egr;

public enum EgrFilialTypes {
    FILIAL(0),
    REPRESENTATION(1);

    private final int value;
    private EgrFilialTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
