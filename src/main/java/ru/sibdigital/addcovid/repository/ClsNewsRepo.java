package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsNews;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClsNewsRepo extends JpaRepository<ClsNews, Long> {
    Optional<ClsNews> findById(Long id);

    @Query(nativeQuery = true,
            value = "SELECT cls_news.*\n" +
                    "       FROM cls_news\n" +
                    "        INNER JOIN\n" +
                    "            (SELECT reg_news_organization.id_news\n" +
                    "            from reg_news_organization\n" +
                    "            WHERE id_organization = :id_organization\n" +
                    "            UNION\n" +
                    "            SELECT path_news_okveds.id_news\n" +
                    "            FROM\n" +
                    "                (SELECT id_news, o.path\n" +
                    "                 FROM reg_news_okved\n" +
                    "                          INNER JOIN okved o on reg_news_okved.id_okved = o.id) AS path_news_okveds\n" +
                    "                    INNER JOIN\n" +
                    "                    (SELECT okved.path\n" +
                    "                     FROM okved\n" +
                    "                              INNER JOIN\n" +
                    "                          (SELECT reg_organization_okved.id_okved\n" +
                    "                           FROM reg_organization_okved\n" +
                    "                           WHERE reg_organization_okved.id_organization = :id_organization) AS id_org_okveds\n" +
                    "                          ON okved.id = id_org_okveds.id_okved) AS path_org_okveds\n" +
                    "                    ON path_news_okveds.path @> path_org_okveds.path\n" +
                    "            UNION\n" +
                    "            SELECT reg_news_status.id_news\n" +
                    "            FROM reg_news_status\n" +
                    "            INNER JOIN\n" +
                    "                (SELECT doc_request.status_review\n" +
                    "                FROM doc_request\n" +
                    "                WHERE id_organization = :id_organization) AS drs\n" +
                    "            ON reg_news_status.status_review = drs.status_review) as news_by_org\n" +
                    "        ON cls_news.id = news_by_org.id_news\n" +
                    "WHERE start_time < :current_time AND end_time > :current_time\n" +
                    "ORDER BY start_time DESC")
    List<ClsNews> getCurrentNewsByOrganization_Id(Long id_organization, Timestamp current_time);

    @Query(nativeQuery = true,
            value = "SELECT cls_news.*\n" +
                    "       FROM cls_news\n" +
                    "        INNER JOIN\n" +
                    "            (SELECT reg_news_organization.id_news\n" +
                    "            from reg_news_organization\n" +
                    "            WHERE id_organization = :id_organization\n" +
                    "            UNION\n" +
                    "            SELECT path_news_okveds.id_news\n" +
                    "            FROM\n" +
                    "                (SELECT id_news, o.path\n" +
                    "                 FROM reg_news_okved\n" +
                    "                          INNER JOIN okved o on reg_news_okved.id_okved = o.id) AS path_news_okveds\n" +
                    "                    INNER JOIN\n" +
                    "                    (SELECT okved.path\n" +
                    "                     FROM okved\n" +
                    "                              INNER JOIN\n" +
                    "                          (SELECT reg_organization_okved.id_okved\n" +
                    "                           FROM reg_organization_okved\n" +
                    "                           WHERE reg_organization_okved.id_organization = :id_organization) AS id_org_okveds\n" +
                    "                          ON okved.id = id_org_okveds.id_okved) AS path_org_okveds\n" +
                    "                    ON path_news_okveds.path @> path_org_okveds.path\n" +
                    "            UNION\n" +
                    "            SELECT reg_news_status.id_news\n" +
                    "            FROM reg_news_status\n" +
                    "            INNER JOIN\n" +
                    "                (SELECT doc_request.status_review\n" +
                    "                FROM doc_request\n" +
                    "                WHERE id_organization = :id_organization) AS drs\n" +
                    "            ON reg_news_status.status_review = drs.status_review) as news_by_org\n" +
                    "        ON cls_news.id = news_by_org.id_news\n" +
                    "WHERE end_time < :current_time\n" +
                    "ORDER BY start_time DESC")
    List<ClsNews> getNewsArchiveByOrganization_Id(Long id_organization, Timestamp current_time);
}