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

    @Query(value = "with org_requests as(\n" +
            "    select dr.* from doc_request as dr\n" +
            "                      inner join (select *\n" +
            "                                  from cls_organization as co\n" +
            "                                  where co.inn = :inn\n" +
            "    ) as co on dr.id_organization = co.id\n" +
            "    where dr.status_review = 1\n" +
            "),\n" +
            "slice_doc_request as(\n" +
            "    select ore.id_organization, ore.id_type_request,  max(ore.time_create) as time_create\n" +
            "    from org_requests as ore\n" +
            "    group by ore.id_organization, ore.id_type_request\n" +
            ")\n" +
            "select dr.*\n" +
            "from slice_doc_request as sdr\n" +
            "         inner join doc_request as dr\n" +
            "                    on (sdr.id_organization, sdr.id_type_request, sdr.time_create) = (dr.id_organization, dr.id_type_request, dr.time_create)\n" +
            "order by time_review desc",
            nativeQuery = true)
    List<DocRequestPrs> getActualizedRequests(String inn);
}
