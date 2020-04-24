package ru.sibdigital.addcovid.model;

public enum StatusRegistration {
    CLOSED(0),
    OPENED(1);

    private final int value;
    private StatusRegistration(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
