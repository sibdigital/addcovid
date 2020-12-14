package ru.sibdigital.addcovid.model;

public enum MailingStatuses {
    EMAIL_SENT((short) 0),
    INVALID_ADDRESS((short) 1),
    EMAIL_NOT_CREATED((short) 2),
    EMAIL_NOT_SENT((short) 3),
    UNKNOWN_ERROR((short) 4),
    MAILING_LIST_NOT_VALID((short) 5);

    private final short value;

    private MailingStatuses(short value) {
        this.value = value;
    }

    public short value() {
        return this.value;
    }
}
