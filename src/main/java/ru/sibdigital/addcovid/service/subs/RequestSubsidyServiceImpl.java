package ru.sibdigital.addcovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.addcovid.repository.subs.TpRequiredSubsidyFileRepo;

import java.util.List;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    private TpRequiredSubsidyFileRepo tpRequiredSubsidyFileRepo;

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    public List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id){
        return tpRequiredSubsidyFileRepo.findAllBySubsidyIdAndIsDeletedFalse(id).orElse(null);
    }

    public Long findLastRequestSubsidyFile(){
        return tpRequestSubsidyFileRepo.findLastSubsidyFile().orElse(null);
    }

    public List<TpRequestSubsidyFile> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id){
        return tpRequestSubsidyFileRepo.findAllRequestSubsidyFilesByRequestAndFileType(request_subsidy_id, file_type_id).orElse(null);
    }

    public TpRequestSubsidyFile findSignatureFile(Long id){
        return tpRequestSubsidyFileRepo.findSignatureFile(id).orElse(null);
    }
}
