package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.model.DocRequestPrs;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocRequestPrsRepo extends JpaRepository<DocRequestPrs, Long> {

    @EntityGraph(attributePaths = {"organization", "department"})
    Optional<List<DocRequestPrs>> findFirst100ByDepartmentAndStatusReviewOrderByTimeCreate(ClsDepartment department, Integer status);

    @Query(value = "SELECT dr.* FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status " +
            " and (trim(org.inn) like %:innOrName% or lower(trim(org.name)) like %:innOrName%) " +
            " ORDER BY dr.time_create ASC  limit 100",
            nativeQuery = true)
    Optional<List<DocRequestPrs>> getFirst100RequestByDepartmentIdAndStatusAndInnOrName(@Param("dep_id") Long departmentId, @Param("status")
            Integer status, @Param("innOrName") String innOrName);

    @Query("SELECT dr FROM DocRequestPrs dr WHERE dr.organization.inn = :inn and dr.statusReview = 1 ORDER BY dr.timeCreate DESC")
    List<DocRequestPrs> getActualizedRequests(String inn);
}
