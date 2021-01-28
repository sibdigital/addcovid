package ru.sibdigital.addcovid.model.egr;

public enum EgrActiveStatus {
    ACTIVE(0),
    NOT_VALID(1), // регистрация признана недействительной, т.е. статусы с кодами 701, 702, 801.
    CEASED(2); // организация прекратила свою деятельность, есть свПрекрЮЛ

    private final int value;
    private EgrActiveStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
