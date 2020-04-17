package ru.sibdigital.addcovid.model;

public enum RequestTypes {
    ORGANIZATION(1),
    BARBERSHOP(2);

    private final int value;
    private RequestTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
