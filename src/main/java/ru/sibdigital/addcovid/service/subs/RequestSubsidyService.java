package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;

import java.util.List;

public interface RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

        void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto);
        List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id);
        Long findLastRequestSubsidyFile();
        List<TpRequestSubsidyFile> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);
        TpRequestSubsidyFile findSignatureFile(Long id);

}
