package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsTypeRequest;

import java.util.List;

@Repository
public interface ClsTypeRequestRepo extends CrudRepository<ClsTypeRequest, Long> {
    public List<ClsTypeRequest> findAllByOrderBySortWeight();

    /**
     * Выборка предписаний для организации из двух частей:
     * 1. Опубликованные(status_publication = 1) предписания, которые совпадают по ОКВЭДу
     * с ОКВЭД организации, а также флаг того, была ли заявка от орагниазции по данному cls_type_request
     * 2. Опубликованные(status_publication = 1) предписания, которые совпадают по ИНН
     * организации, а также флаг того, была ли заявка от орагниазции по данному cls_type_request
     * общая сортировка: сначала те предписания, по которым заявки не подавались, потом по времени публикации и весу.
     * @param orgId
     * @return
     */
    @Query(nativeQuery = true, value = "" +
            "with org_okveds as (select id_okved from reg_organization_okved where id_organization = :orgId), \n" +
            "     org_prescriptions as (select id_type_request from doc_request dr where dr.id_organization = :orgId) \n" +
            "select * \n" +
            "from ( \n" +
            "         select ctr.*, ctr.id in (select id_type_request from org_prescriptions) as is_exist \n" +
            "         from cls_type_request ctr \n" +
            "         where ctr.status_publication = 1 \n" +
            "           and exists (select id_okved \n" +
            "                       from org_okveds \n" +
            "                       where org_okveds.id_okved in ( \n" +
            "                           select CAST(jsonb_array_elements_text( \n" +
            "                                   jsonb_extract_path(ctr.additional_fields, 'okvedIds') \n" +
            "                               ) as uuid)) \n" +
            "             ) \n" +
            "         union \n" +
            "         select ctr.*, ctr.id in (select id_type_request from org_prescriptions) as is_exist \n" +
            "         from cls_type_request ctr \n" +
            "         where ctr.status_publication = 1 \n" +
            "           and :orgId in ( \n" +
            "             select CAST(jsonb_array_elements_text( \n" +
            "                     jsonb_extract_path(ctr.additional_fields, 'organizationIds') \n" +
            "                 ) as int)) \n" +
            "       ) as predp \n" +
            "order by is_exist," +
            "         time_publication desc," +
            "         sort_weight")
    List<ClsTypeRequest> getPrescriptionsByOrganizationId(Long orgId);
}
