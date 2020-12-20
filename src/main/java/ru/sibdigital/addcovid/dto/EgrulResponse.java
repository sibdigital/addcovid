package ru.sibdigital.addcovid.dto;

import ru.sibdigital.addcovid.dto.egrul.EGRUL;

public class EgrulResponse {

    private String message;
    private boolean isFinded;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFinded() {
        return isFinded;
    }

    public void setFinded(boolean finded) {
        isFinded = finded;
    }

    public class Data{
        private String inn;
        private String ogrn;
        private String name;
        private String email;
        private String shortName;

        public Data(String inn, String ogrn, String name, String shortName, String email){
            this.inn = inn;
            this.ogrn = ogrn;
            this.name = name;
            this.shortName = shortName;
            this.email = email;
        }

        public String getInn() {
            return inn;
        }

        public String getOgrn() {
            return ogrn;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getShortName() {
            return shortName;
        }
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void build(EGRUL.СвЮЛ sved) {
        String inn = sved.getИНН();
        String ogrn = sved.getОГРН();
        String name = sved.getСвНаимЮЛ() != null ? sved.getСвНаимЮЛ().getНаимЮЛПолн() : "";
        String shortName = sved.getСвНаимЮЛ() != null ? sved.getСвНаимЮЛ().getНаимЮЛСокр() : "";
        String email = sved.getСвАдрЭлПочты() != null ? sved.getСвАдрЭлПочты().getEMail() : "";

        this.data = new Data(inn, ogrn, name, shortName, email);
        isFinded = true;
    }
    public void empty(String message) {
        this.message = message;
        this.data = new Data("", "", "", "", "");
        isFinded = false;
    }
}
