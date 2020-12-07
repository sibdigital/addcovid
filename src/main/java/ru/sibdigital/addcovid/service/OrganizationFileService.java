package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.addcovid.repository.RegOrganizationFileRepo;

@Service
@Slf4j
public class OrganizationFileService {
    @Autowired
    RegOrganizationFileRepo regOrganizationFileRepo;

    @Transactional
    public void updateFileStatusById(int id){
        regOrganizationFileRepo.setFileIsDeletedTrueById(id);

    }
}
