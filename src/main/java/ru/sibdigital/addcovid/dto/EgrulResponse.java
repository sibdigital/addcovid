package ru.sibdigital.addcovid.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.sibdigital.addcovid.dto.egrul.EGRUL;
import ru.sibdigital.addcovid.model.OrganizationTypes;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.model.classifier.gov.RegFilial;
import ru.sibdigital.addcovid.model.egr.EgrFilialTypes;
import ru.sibdigital.addcovid.utils.JuridicalUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EgrulResponse {

    private static ObjectMapper mapper = new ObjectMapper();

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
        private Long id;
        private Long filialId;
        private String inn;
        private String ogrn;
        private String kpp;
        private String name;
        private String email;
        private String shortName;
        private String jurAddress;
        private int type;
        private List<Data> filials;

        public Data(Long id, String inn, String ogrn, String kpp, String name, String shortName, String email, String jurAddress, int type){
            this.id = id;
            this.inn = inn;
            this.ogrn = ogrn;
            this.kpp = kpp;
            this.name = name;
            this.shortName = shortName;
            this.email = email;
            this.jurAddress = jurAddress;
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public Long getFilialId() {
            return filialId;
        }

        public void setFilialId(Long filialId) {
            this.filialId = filialId;
        }

        public String getInn() {
            return inn;
        }

        public String getOgrn() {
            return ogrn;
        }

        public String getKpp() {
            return kpp;
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

        public String getShortName() {
            return shortName;
        }

        public int getType() {
            return type;
        }

        public List<Data> getFilials() {
            return filials;
        }

        public void setFilials(List<Data> filials) {
            this.filials = filials;
        }
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void build(RegEgrul egrul) {
        try {
            EGRUL.СвЮЛ sved = mapper.readValue(egrul.getData(), EGRUL.СвЮЛ.class);

            Long id = egrul.getId();
            String inn = egrul.getInn();
            String ogrn = egrul.getOgrn();
            String kpp = egrul.getKpp();
            String name = sved.getСвНаимЮЛ() != null ? sved.getСвНаимЮЛ().getНаимЮЛПолн() : "";
            String shortName = sved.getСвНаимЮЛ() != null ? sved.getСвНаимЮЛ().getНаимЮЛСокр() : "";
            String email = sved.getСвАдрЭлПочты() != null ? sved.getСвАдрЭлПочты().getEMail() : "";
            String jurAddress = JuridicalUtils.constructJuridicalAdress(sved);

            List<Data> filials = new ArrayList<>();
            if (egrul.getRegFilials() != null) {
                for (RegFilial regFilial: egrul.getRegFilials()) {
                    String kppFilial = regFilial.getKpp();
                    String nameFilial = !StringUtils.isEmpty(regFilial.getFullName()) ? regFilial.getFullName() : name;
                    String shortNameFilial = nameFilial;
                    int type = 0;
                    jurAddress = "";
                    if (regFilial.getType().equals(EgrFilialTypes.FILIAL.getValue())) {
                        type = OrganizationTypes.FILIATION.getValue();
                        EGRUL.СвЮЛ.СвПодразд.СвФилиал svedFilial = mapper.readValue(regFilial.getData(), EGRUL.СвЮЛ.СвПодразд.СвФилиал.class);
                        jurAddress = JuridicalUtils.constructAddress(svedFilial.getАдрМНРФ());
                    } else if (regFilial.getType().equals(EgrFilialTypes.REPRESENTATION.getValue())) {
                        type = OrganizationTypes.REPRESENTATION.getValue();
                        EGRUL.СвЮЛ.СвПодразд.СвПредстав svedPredstav = mapper.readValue(regFilial.getData(), EGRUL.СвЮЛ.СвПодразд.СвПредстав.class);
                        jurAddress = JuridicalUtils.constructAddress(svedPredstav.getАдрМНРФ());
                    }
                    Data data = new Data(id, inn, ogrn, kppFilial, nameFilial, shortNameFilial, "", jurAddress, type);
                    data.setFilialId(regFilial.getId());
                    filials.add(data);
                }
            }

            this.data = new Data(id, inn, ogrn, kpp, name, shortName, email, jurAddress, OrganizationTypes.JURIDICAL.getValue());
            data.setFilials(filials);
            isFinded = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void empty(String message) {
        this.message = message;
        this.data = new Data(null, "", "", "", "", "", "", "", 0);
        isFinded = false;
    }
}
