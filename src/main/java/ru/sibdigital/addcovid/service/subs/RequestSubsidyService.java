package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;

import java.util.List;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
        List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id);
        Long findLastRequestSubsidyFile();
        List<TpRequestSubsidyFile> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);
        TpRequestSubsidyFile findSignatureFile(Long id);
}
