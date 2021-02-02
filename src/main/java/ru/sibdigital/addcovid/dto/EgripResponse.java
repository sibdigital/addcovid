package ru.sibdigital.addcovid.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sibdigital.addcovid.dto.egrip.EGRIP;
import ru.sibdigital.addcovid.dto.egrip.EGRIP.СвИП;
import ru.sibdigital.addcovid.model.OrganizationTypes;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.utils.JuridicalUtils;

import java.util.ArrayList;
import java.util.List;

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
        private int type;

        public Data(Long id, String inn, String ogrn, String name, String email, String jurAddress, int type) {
            this.id = id;
            this.inn = inn;
            this.ogrn = ogrn;
            this.name = name;
            this.email = email;
            this.jurAddress = jurAddress;
            this.type = type;
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

        public int getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void build(List<RegEgrip> egrips) {
        try {
            this.data = new ArrayList<>();
            for (RegEgrip egrip: egrips) {
                СвИП sved = mapper.readValue(egrip.getData(), EGRIP.СвИП.class);

                Long id = egrip.getId();
                String inn = egrip.getInn();
                String ogrn = egrip.getOgrn();
                String name = "";
                int type = 0;
                if (sved.getКодВидИП().equals("1")) {
                    type = OrganizationTypes.IP.getValue();
                    name += "ИП ";
                } else if (sved.getКодВидИП().equals("2")) {
                    type = OrganizationTypes.KFH.getValue();
                    name += "КФХ ";
                }
                if (sved.getСвФЛ() != null && sved.getСвФЛ().getФИОРус() != null) {
                    name += sved.getСвФЛ().getФИОРус().getФамилия()
                            + " " + sved.getСвФЛ().getФИОРус().getИмя()
                            + " " + sved.getСвФЛ().getФИОРус().getОтчество();
                }
                String email = sved.getСвАдрЭлПочты() != null ? sved.getСвАдрЭлПочты().getEMail() : "";
                String jurAddress = JuridicalUtils.constructJuridicalAdress(sved);

                data.add(new Data(id, inn, ogrn, name, email, jurAddress, type));
            }

            isFinded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void empty(String message) {
        this.message = message;
        this.data = new ArrayList<>(0);
        isFinded = false;
    }
}
