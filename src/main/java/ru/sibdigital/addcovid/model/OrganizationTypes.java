package ru.sibdigital.addcovid.model;

public enum OrganizationTypes {
    JURIDICAL(1),
    PHYSICAL(2);

    private final int value;

    private OrganizationTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
