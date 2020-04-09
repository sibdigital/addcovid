package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"docPersonSet", "organization", "department" ,"docAddressFact"})
    Optional<List<DocRequest>> findFirst100ByDepartmentAndStatusReviewOrderByTimeCreate(ClsDepartment department, Integer status);

    //@Query("SELECT dr FROM DocRequest dr WHERE dr.department.id = :dep_id AND dr.statusReview = :status")
    @Query(value = "SELECT dr.* FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status ORDER BY dr.time_create ASC limit 100" , nativeQuery = true)
    Optional<List<DocRequest>> getAllByDepartmentId(@Param("dep_id")Long departmentId, @Param("status") Integer status);

    @Query(value = "SELECT dr.* FROM doc_request dr, cls_organization org WHERE  dr.id_organization = org.id " +
            " and dr.id_department = :dep_id AND dr.status_review = :status " +
            " and (trim(org.inn) like %:innOrName% or lower(trim(org.name)) like %:innOrName%) " +
            " ORDER BY dr.time_create ASC  limit 100",
            nativeQuery = true)
    Optional<List<DocRequest>> getFirst100RequestByDepartmentIdAndStatusAndInnOrName(@Param("dep_id")Long departmentId, @Param("status")
            Integer status, @Param("innOrName")String innOrName);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInnAndStatus(@Param("inn")String inn, @Param("status") Integer status);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn AND dr.statusReview = :status ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrnAndStatus(@Param("ogrn")String ogrn, @Param("status") Integer status);

    @Query("SELECT dr FROM DocRequest dr WHERE  dr.organization.inn = :inn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByInn(@Param("inn")String inn);

    @Query(value = "SELECT dr FROM DocRequest dr WHERE  dr.organization.ogrn = :ogrn ORDER BY dr.timeCreate DESC")
    Optional<List<DocRequest>> getLastRequestByOgrn(@Param("ogrn")String ogrn);



    @Query(nativeQuery = true, value = "SELECT count(*) FROM ( SELECT DISTINCT firstname, lastname, patronymic FROM doc_person WHERE id_request IN (SELECT id FROM doc_request WHERE status_review = 1)) AS s;")
    public Long getTotalApprovedPeople();


    @Query(nativeQuery = true, value = "select d.name, d.id, coalesce(neobr, 0) as neobr, coalesce(utv, 0) as utv, coalesce(otkl, 0) as otkl from cls_department as d" +
            "                                                                                                              left join ( select id_department, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl" +
            "                                                                                                                          from (" +
            "                                                                                                                                   select id_department," +
            "                                                                                                                                          status_review," +
            "                                                                                                                                          sum(neobr) as neobr," +
            "                                                                                                                                          sum(utv)   as utv," +
            "                                                                                                                                          sum(otkl)  as otkl" +
            "                                                                                                                                   from (" +
            "                                                                                                                                            select id_department, status_review, 0 as neobr, 1 as utv, 0 as otkl" +
            "                                                                                                                                            from doc_request" +
            "                                                                                                                                            where status_review = 1" +
            "                                                                                                                                            union" +
            "                                                                                                                                            select id_department, status_review, 0 as neobr, 0 as utv, 1 as otkl" +
            "                                                                                                                                            from doc_request" +
            "                                                                                                                                            where status_review = 2" +
            "                                                                                                                                            union" +
            "                                                                                                                                            select id_department, status_review, 1 as neobr, 0 as utv, 0 as otkl" +
            "                                                                                                                                            from doc_request" +
            "                                                                                                                                            where status_review <> 2" +
            "                                                                                                                                              and status_review <> 1" +
            "                                                                                                                                        ) as s" +
            "                                                                                                                                   group by id_department, status_review" +
            "                                                                                                                               ) as m group by id_department" +
            ") as ss on d.id = ss.id_department order by d.id")
    public List<Map<String, Object>> getRequestStatisticForEeachDepartment();
    
    

    /*
select
        docrequest0_.id as id1_5_0_,
        clsdepartm1_.id as id1_0_1_,
        clsorganiz2_.id as id1_1_2_,
        docpersons3_.id as id1_4_3_,
        docaddress4_.id as id1_3_4_,
        docrequest0_.attachment_path as attachme2_5_0_,
        docrequest0_.id_department as id_depa17_5_0_,
        docrequest0_.is_agree as is_agree3_5_0_,
        docrequest0_.is_protect as is_prote4_5_0_,
        docrequest0_.old_department_id as old_depa5_5_0_,
        docrequest0_.org_hash_code as org_hash6_5_0_,
        docrequest0_.id_organization as id_orga18_5_0_,
        docrequest0_.person_office_cnt as person_o7_5_0_,
        docrequest0_.person_remote_cnt as person_r8_5_0_,
        docrequest0_.person_slry_save_cnt as person_s9_5_0_,
        docrequest0_.reject_comment as reject_10_5_0_,
        docrequest0_.req_basis as req_bas11_5_0_,
        docrequest0_.status_import as status_12_5_0_,
        docrequest0_.status_review as status_13_5_0_,
        docrequest0_.time_create as time_cr14_5_0_,
        docrequest0_.time_import as time_im15_5_0_,
        docrequest0_.time_review as time_re16_5_0_,
        clsdepartm1_.description as descript2_0_1_,
        clsdepartm1_.name as name3_0_1_,
        clsdepartm1_.status_import as status_i4_0_1_,
        clsdepartm1_.time_import as time_imp5_0_1_,
        clsorganiz2_.address_jur as address_2_1_2_,
        clsorganiz2_.email as email3_1_2_,
        clsorganiz2_.inn as inn4_1_2_,
        clsorganiz2_.name as name5_1_2_,
        clsorganiz2_.ogrn as ogrn6_1_2_,
        clsorganiz2_.okved as okved7_1_2_,
        clsorganiz2_.okved_add as okved_ad8_1_2_,
        clsorganiz2_.phone as phone9_1_2_,
        clsorganiz2_.short_name as short_n10_1_2_,
        clsorganiz2_.status_import as status_11_1_2_,
        clsorganiz2_.time_import as time_im12_1_2_,
        docpersons3_.id_request as id_reque5_4_3_,
        docpersons3_.firstname as firstnam2_4_3_,
        docpersons3_.lastname as lastname3_4_3_,
        docpersons3_.patronymic as patronym4_4_3_,
        docpersons3_.id_request as id_reque5_4_0__,
        docpersons3_.id as id1_4_0__,
        docaddress4_.address_fact as address_2_3_4_,
        docaddress4_.id_request as id_reque4_3_4_,
        docaddress4_.person_office_fact_cnt as person_o3_3_4_,
        docaddress4_.id_request as id_reque4_3_1__,
        docaddress4_.id as id1_3_1__
    from
        public.doc_request docrequest0_
    left outer join
        public.cls_department clsdepartm1_
            on docrequest0_.id_department=clsdepartm1_.id
    left outer join
        public.cls_organization clsorganiz2_
            on docrequest0_.id_organization=clsorganiz2_.id
    left outer join
        public.doc_person docpersons3_
            on docrequest0_.id=docpersons3_.id_request
    left outer join
        public.doc_address_fact docaddress4_
            on docrequest0_.id=docaddress4_.id_request
    where
        docrequest0_.id_department=?
        and docrequest0_.status_review=?
    order by
        docrequest0_.time_create asc
    * */


}
