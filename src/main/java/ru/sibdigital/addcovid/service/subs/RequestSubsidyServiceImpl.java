package ru.sibdigital.addcovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.repository.subs.TpRequiredSubsidyFileRepo;

import java.util.List;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    private TpRequiredSubsidyFileRepo tpRequiredSubsidyFileRepo;

    public List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id){
        return tpRequiredSubsidyFileRepo.findAllBySubsidyIdAndIsDeletedFalse(id).orElse(null);
    }
}
