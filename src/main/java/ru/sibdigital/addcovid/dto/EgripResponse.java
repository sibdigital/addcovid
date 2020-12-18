package ru.sibdigital.addcovid.dto;

import ru.sibdigital.addcovid.dto.egrip.EGRIP;
import ru.sibdigital.addcovid.dto.egrip.EGRIP.СвИП;
import ru.sibdigital.addcovid.dto.egrul.EGRUL;

public class EgripResponse {

    private String message;
    private boolean isPossiblySelfEmployed;
    private boolean isFinded;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPossiblySelfEmployed() {
        return isPossiblySelfEmployed;
    }

    public void setPossiblySelfEmployed(boolean possiblySelfEmployed) {
        isPossiblySelfEmployed = possiblySelfEmployed;
    }

    public boolean isFinded() {
        return isFinded;
    }

    public void setFinded(boolean finded) {
        isFinded = finded;
    }

    public class Data {
        private String inn;
        private String name;
        private String email;

        public Data(String inn, String name, String email) {
            this.inn = inn;
            this.name = name;
            this.email = email;
        }

        public String getInn() {
            return inn;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMessage() {
            return message;
        }
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void build(СвИП sved) {
        String inn = sved.getИННФЛ();
        String name = "";
        if (sved.getСвФЛ() != null && sved.getСвФЛ().getФИОРус() != null) {
            name = sved.getСвФЛ().getФИОРус().getФамилия()
                    + " " + sved.getСвФЛ().getФИОРус().getИмя()
                    + " " + sved.getСвФЛ().getФИОРус().getОтчество();
        }
        String email = sved.getСвАдрЭлПочты() != null ? sved.getСвАдрЭлПочты().getEMail() : "";

        this.data = new Data(inn, name, email);
        isFinded = true;
    }

    public void empty(String message) {
        this.message = message;
        this.data = new  Data("", "", "");
        isFinded = false;
    }
}
