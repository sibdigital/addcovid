package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;
import ru.sibdigital.addcovid.model.RegOrganizationInspectionFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface RegOrganizationInspectionFileRepo extends JpaRepository<RegOrganizationInspectionFile, Long> {
    Optional<List<RegOrganizationInspectionFile>> findRegOrganizationInspectionFilesByOrganizationInspectionAndIsDeleted(RegOrganizationInspection inspection, Boolean deleted);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE reg_organization_inspection_file SET is_deleted = true WHERE id in (:inspectionFileIds)",
            nativeQuery = true
    )
    void updateFilesAsDeleted(Set<Long> inspectionFileIds);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE reg_organization_inspection_file SET is_deleted = false, id_organization_inspection = :idInspection WHERE id in (:inspectionFileIds)",
            nativeQuery = true
    )
    void updateFilesAsNotDeletedAndSetIdInspection(Set<Long> inspectionFileIds, Long idInspection);
}
