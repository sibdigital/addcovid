package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
    void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto);
}
