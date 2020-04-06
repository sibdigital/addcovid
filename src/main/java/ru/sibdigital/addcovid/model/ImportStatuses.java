package ru.sibdigital.addcovid.model;

public enum ImportStatuses {
    SUCCESS(0),
    FILE_ERROR(1),
    FATAL_ERROR(2);

    private final int value;
    private ImportStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
