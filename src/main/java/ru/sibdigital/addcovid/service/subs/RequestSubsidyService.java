package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
    void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto);
    DocRequestSubsidy saveDocRequestSubsidyDraft(DocRequestSubsidyPostDto postFormDto);
}
