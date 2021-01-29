package ru.sibdigital.addcovid.service.crassifier;

import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;

import java.util.List;


public interface EgrulService {

    RegEgrul getEgrul(String inn);

    List<RegEgrip> getEgrip(String inn);
}
