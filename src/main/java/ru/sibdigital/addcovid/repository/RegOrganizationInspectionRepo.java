package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegOrganizationInspectionRepo extends JpaRepository<RegOrganizationInspection, Long> {

    Optional<List<RegOrganizationInspection>> findRegOrganizationInspectionsByOrganization(ClsOrganization organization);
    Optional<List<RegOrganizationInspection>> findRegOrganizationInspectionsByOrganizationAndControlAuthority_IsDeleted(ClsOrganization organization, Boolean isDeleted);

}
