package ru.sibdigital.addcovid.dto;

import ru.sibdigital.addcovid.dto.egrip.EGRIP;

public class EgripResponse {

    private EGRIP.СвИП data;

    public EGRIP.СвИП getData() {
        return data;
    }

    public void setData(EGRIP.СвИП data) {
        this.data = data;
    }
}
