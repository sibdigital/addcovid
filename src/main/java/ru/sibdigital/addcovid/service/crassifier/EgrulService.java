package ru.sibdigital.addcovid.service.crassifier;

import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;


public interface EgrulService {

    RegEgrul getEgrul(String inn);

    RegEgrip getEgrip(String inn);
}
