package ru.sibdigital.addcovid.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sibdigital.addcovid.dto.egrip.EGRIP;
import ru.sibdigital.addcovid.dto.egrip.EGRIP.СвИП;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.utils.JuridicalUtils;

public class EgripResponse {

    private static ObjectMapper mapper = new ObjectMapper();

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
        private Long id;
        private String inn;
        private String ogrn;
        private String name;
        private String email;
        private String jurAddress;

        public Data(Long id, String inn, String ogrn, String name, String email, String jurAddress) {
            this.id = id;
            this.inn = inn;
            this.ogrn = ogrn;
            this.name = name;
            this.email = email;
            this.jurAddress = jurAddress;
        }

        public Long getId() {
            return id;
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

        public String getJurAddress() {
            return jurAddress;
        }

        public String getMessage() {
            return message;
        }
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void build(RegEgrip egrip) {
        try {
            СвИП sved = mapper.readValue(egrip.getData(), EGRIP.СвИП.class);

            Long id = egrip.getId();
            String inn = sved.getИННФЛ();
            String ogrn = sved.getОГРНИП();
            String name = "";
            if (sved.getСвФЛ() != null && sved.getСвФЛ().getФИОРус() != null) {
                name = sved.getСвФЛ().getФИОРус().getФамилия()
                        + " " + sved.getСвФЛ().getФИОРус().getИмя()
                        + " " + sved.getСвФЛ().getФИОРус().getОтчество();
            }
            String email = sved.getСвАдрЭлПочты() != null ? sved.getСвАдрЭлПочты().getEMail() : "";
            String jurAddress = JuridicalUtils.constructJuridicalAdress(sved);

            this.data = new Data(id, inn, ogrn, name, email, jurAddress);
            isFinded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void empty(String message) {
        this.message = message;
        this.data = new  Data(null, "", "", "", "", "");
        isFinded = false;
    }
}
