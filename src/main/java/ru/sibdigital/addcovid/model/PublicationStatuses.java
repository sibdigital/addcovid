package ru.sibdigital.addcovid.model;

public enum PublicationStatuses {
    NOT_PUBLISHED(0),
    PUBLISHED(1);

    private final int value;
    private PublicationStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
