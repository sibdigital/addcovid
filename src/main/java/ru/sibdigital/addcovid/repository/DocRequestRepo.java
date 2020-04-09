package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.DocRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface DocRequestRepo extends JpaRepository<DocRequest, Long> {
    Optional<DocRequest> getTopByOrganization(ClsOrganization clsOrganization);
    Optional<DocRequest> getTopByOrgHashCode(String sha256code);
    Optional<List<DocRequest>> getAllByDepartmentAndStatusReview(ClsDepartment department, Integer status);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.department.id = :dep_id AND dr.statusReview = :status")
    Optional<List<DocRequest>> getAllByDepartmentId(@Param("dep_id")Long departmentId, @Param("status") Integer status);





    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInnAndStatus(@Param("inn")String inn, @Param("status") Integer status);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrnAndStatus(@Param("ogrn")String ogrn, @Param("status") Integer status);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInn(@Param("inn")String inn);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrn(@Param("ogrn")String ogrn);







    @Query(nativeQuery = true, value = "select d.name, d.id, coalesce(neobr, 0) as neobr, coalesce(utv, 0) as utv, coalesce(otkl, 0) as otkl from cls_department as d" +
            "                  left join ( select id_department, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl" +
            "                              from (" +
            "                                       select id_department," +
            "                                              sum(neobr) as neobr," +
            "                                              sum(utv)   as utv," +
            "                                              sum(otkl)  as otkl" +
            "                                       from (" +
            "                                                select id_department, 0 as neobr, 1 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 1" +
            "                                                union all" +
            "                                                select id_department, 0 as neobr, 0 as utv, 1 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review = 2" +
            "                                                union all" +
            "                                                select id_department, 1 as neobr, 0 as utv, 0 as otkl" +
            "                                                from doc_request" +
            "                                                where status_review <> 2" +
            "                                                  and status_review <> 1" +
            "                                            ) as s" +
            "                                       group by id_department" +
            "                                   ) as m group by id_department" +
            ") as ss on d.id = ss.id_department order by d.id")
    public List<Map<String, Object>> getRequestStatisticForEeachDepartment();
    
    
    @Query(nativeQuery = true, value = "SELECT date_trunc('day',doc_request.time_create) as date, COUNT(*) AS total FROM doc_request GROUP BY date_trunc('day',doc_request.time_create);")
    public List<Map<String, Object>> getStatisticForEachDay();
    
    
    

}
