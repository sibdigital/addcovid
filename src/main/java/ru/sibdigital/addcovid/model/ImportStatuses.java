package ru.sibdigital.addcovid.model;

public enum ImportStatuses {
    SUCCESS(0),
    IMPORTED(1),
    READY_TO_IMPORT(5);

    private final int value;
    private ImportStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
