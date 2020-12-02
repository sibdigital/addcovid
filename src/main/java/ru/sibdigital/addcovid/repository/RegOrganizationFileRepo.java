package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationFile;
import ru.sibdigital.addcovid.model.RegOrganizationOkved;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegOrganizationFileRepo extends JpaRepository<RegOrganizationFile, Long> {
    List<RegOrganizationFile> findRegOrganizationFileByOrganization(ClsOrganization organization);
}
