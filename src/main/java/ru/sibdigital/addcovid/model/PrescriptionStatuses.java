package ru.sibdigital.addcovid.model;

public enum PrescriptionStatuses {
    NOT_PUBLISHED(0),
    PUBLISHED(1);

    private final int value;
    private PrescriptionStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
