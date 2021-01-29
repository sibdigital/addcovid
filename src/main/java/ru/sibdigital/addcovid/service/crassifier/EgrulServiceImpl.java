package ru.sibdigital.addcovid.service.crassifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.model.egr.EgrActiveStatus;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgripRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgrulRepo;

import java.util.List;

@Service
public class EgrulServiceImpl implements EgrulService {

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    public RegEgrul getEgrul(String inn) {
        if (inn == null || inn.isBlank()) {
            return null;
        }
        RegEgrul regEgrul = regEgrulRepo.findByInnAndActiveStatus(inn, EgrActiveStatus.ACTIVE.getValue());
        return regEgrul;
    }

    public List<RegEgrip> getEgrip(String inn) {
        if (inn == null || inn.isBlank()) {
            return null;
        }
        List<RegEgrip> regEgrips = regEgripRepo.findByInnAndActiveStatus(inn, EgrActiveStatus.ACTIVE.getValue());
        return regEgrips;
    }
}
