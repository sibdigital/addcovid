package ru.sibdigital.addcovid.model;

public enum ActivityStatuses {
    ACTIVE(1),
    HISTORICAL(0);

    private final int value;
    private ActivityStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
