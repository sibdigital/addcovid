package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sibdigital.addcovid.model.subs.ClsSubsidy;

import java.util.List;

public interface ClsSubsidyRepo extends JpaRepository<ClsSubsidy, Long> {

    @Query(nativeQuery = true,
    value = "WITH subsidies_ids AS (\n" +
            "    SELECT stso.id_subsidy\n" +
            "    FROM reg_organization_okved roo\n" +
            "             INNER JOIN subs.tp_subsidy_okved stso\n" +
            "                        ON roo.id_organization = :id_organization AND roo.id_okved = stso.id_okved AND stso.is_deleted = false\n" +
            "                            AND\n" +
            "                           CASE\n" +
            "                               WHEN stso.id_type_organization is null OR stso.id_type_organization = :id_type_organization\n" +
            "                                   THEN true\n" +
            "                               ELSE false END\n" +
            "    GROUP BY stso.id_subsidy\n" +
            ")\n" +
            "SELECT scs.*\n" +
            "FROM subs.cls_subsidy scs\n" +
            "    INNER JOIN subsidies_ids\n" +
            "        ON scs.id = subsidies_ids.id_subsidy AND scs.is_deleted = false;")
    List<ClsSubsidy> getListSubsidyForOrganization(@Param("id_organization") Long idOrganization, @Param("id_type_organization") Integer idTypeOrganization);


    @Query(nativeQuery = true,
    value = "WITH org_subsidies_ids AS (\n" +
            "    SELECT stso.id_subsidy\n" +
            "    FROM reg_organization_okved roo\n" +
            "             INNER JOIN subs.tp_subsidy_okved stso\n" +
            "                        ON roo.id_organization = :id_organization AND roo.id_okved = stso.id_okved AND stso.is_deleted = false\n" +
            "                            AND\n" +
            "                           CASE\n" +
            "                               WHEN stso.id_type_organization is null OR stso.id_type_organization = :id_type_organization\n" +
            "                                   THEN true\n" +
            "                               ELSE false END\n" +
            "    GROUP BY stso.id_subsidy\n" +
            "),\n" +
            "filled_subs_ids AS (\n" +
            "    SELECT sdrs.id_subsidy\n" +
            "    FROM subs.doc_request_subsidy sdrs\n" +
            "        INNER JOIN subs.cls_subsidy_request_status sub_status\n" +
            "            ON sdrs.id_subsidy_request_status = sub_status.id AND sdrs.id_organization = :id_organization AND sub_status.is_block_request\n" +
            "    GROUP BY sdrs.id_subsidy\n" +
            "),\n" +
            "available_subs_ids AS (\n" +
            "    SELECT osi.id_subsidy\n" +
            "    FROM org_subsidies_ids osi\n" +
            "        LEFT JOIN filled_subs_ids fsi\n" +
            "                ON osi.id_subsidy = fsi.id_subsidy\n" +
            "    WHERE fsi.id_subsidy IS NULL\n" +
            ")\n" +
            "SELECT scs.*\n" +
            "FROM subs.cls_subsidy scs\n" +
            "    INNER JOIN available_subs_ids asi\n" +
            "        ON scs.id = asi.id_subsidy AND scs.is_deleted = false")
    List<ClsSubsidy> getAvailableSubsidiesForOrganization(@Param("id_organization") Long idOrganization, @Param("id_type_organization") Integer idTypeOrganization);
}
