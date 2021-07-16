package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;

import java.util.Date;
import java.util.List;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto);
    List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id);
    List<TpRequestSubsidyFile> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);
    TpRequestSubsidyFile findSignatureFile(Long id);
    DocRequestSubsidy saveDocRequestSubsidyDraft(DocRequestSubsidyPostDto postFormDto);

    Long getWaitingStartTaskExecution(Date date);
}
