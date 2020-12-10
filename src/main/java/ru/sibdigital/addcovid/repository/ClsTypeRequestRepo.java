package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsTypeRequest;

import java.util.List;

@Repository
public interface ClsTypeRequestRepo extends CrudRepository<ClsTypeRequest, Long> {
    public List<ClsTypeRequest> findAllByOrderBySortWeight();

    @Query(nativeQuery = true, value = "select * " +
            "from " +
            "   cls_type_request ctr " +
            "where " +
            "   ctr.status_publication = 1 " +
            "   and exists (select id_okved " +
            "       from " +
            "           (select id_okved from reg_organization_okved where id_organization = :orgId) as org_okveds " +
            "       where " +
            "           org_okveds.id_okved in (select CAST(jsonb_array_elements_text(jsonb_extract_path((select additional_fields from cls_type_request where id = ctr.id), 'okvedIds')) as uuid)) " +
            "   ) " +
            "   and not exists (select dr.id " +
            "       from " +
            "           doc_request dr " +
            "       where " +
            "           dr.id_organization = :orgId" +
            "           and dr.id_type_request = ctr.id " +
            "           and exists (select id_okved " +
            "               from" +
            "                   (select id_okved from reg_organization_okved where id_organization = dr.id_organization) as org_okveds " +
            "               where" +
            "                   org_okveds.id_okved in (select CAST(jsonb_array_elements_text(jsonb_extract_path((select additional_fields from cls_type_request where id = ctr.id), 'okvedIds')) as uuid)) " +
            "           )" +
            "   )")
    List<ClsTypeRequest> getPrescriptionsByOrganizationId(Long orgId);
}
