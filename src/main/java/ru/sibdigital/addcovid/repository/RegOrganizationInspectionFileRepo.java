package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;
import ru.sibdigital.addcovid.model.RegOrganizationInspectionFile;

import java.util.List;
import java.util.Optional;


@Repository
public interface RegOrganizationInspectionFileRepo extends JpaRepository<RegOrganizationInspectionFile, Long> {
    Optional<List<RegOrganizationInspectionFile>> findRegOrganizationInspectionFilesByOrganizationInspectionAndIsDeleted(RegOrganizationInspection inspection, Boolean deleted);
}
