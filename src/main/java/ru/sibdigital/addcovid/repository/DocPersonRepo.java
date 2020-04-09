package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocPerson;

import java.util.Map;

@Repository
public interface DocPersonRepo extends JpaRepository<DocPerson, Long> {

    @Query(nativeQuery = true, value = "select count(*) from ( select distinct lastname, firstname, patronymic from doc_person) as s")
    public Long getTotalPeople();

    @Query(nativeQuery = true, value = "SELECT count(*) FROM ( SELECT DISTINCT firstname, lastname, patronymic FROM doc_person WHERE id_request IN (SELECT id FROM doc_request WHERE status_review = :status)) AS s;")
    public Long getTotalApprovedPeopleByReviewStatus(@Param("status") int status);

    @Query(nativeQuery = true, value = "select sum(neobr) as awaiting, sum(utv) as accepted, sum(otkl) as declined from doc_person as d\n" +
            "                                                                        left join ( select id_req, max(neobr) as neobr, max(utv) as utv, max(otkl) as otkl\n" +
            "                                                                                    from (\n" +
            "                                                                                             select id_req,\n" +
            "                                                                                                    sum(neobr) as neobr,\n" +
            "                                                                                                    sum(utv)   as utv,\n" +
            "                                                                                                    sum(otkl)  as otkl\n" +
            "                                                                                             from (\n" +
            "                                                                                                      select id as id_req, 0 as neobr, 1 as utv, 0 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review = 1\n" +
            "                                                                                                      union all\n" +
            "                                                                                                      select id as id_req, 0 as neobr, 0 as utv, 1 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review = 2\n" +
            "                                                                                                      union all\n" +
            "                                                                                                      select id as id_req, 1 as neobr, 0 as utv, 0 as otkl\n" +
            "                                                                                                      from doc_request\n" +
            "                                                                                                      where status_review <> 2\n" +
            "                                                                                                        and status_review <> 1\n" +
            "                                                                                                  ) as s\n" +
            "                                                                                             group by id_req\n" +
            "                                                                                         ) as m group by id_req\n" +
            ") as ss on d.id_request = ss.id_req;")
    public Map<String, Long> getTotalPeopleStatistic();

}
