package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsPrescription;

import java.util.List;
import java.util.Set;

@Repository
public interface ClsPrescriptionRepo extends JpaRepository<ClsPrescription, Long> {

    @Query(nativeQuery = true, value = "" +
            "select * " +
            "from cls_prescription cp " +
            "where cp.id_type_request in (:typeRequestIds) " +
            "   and cp.status = :status " +
            "   and cp.id not in (select id_prescription from reg_organization_prescription where id_organization = :orgId)" +
            "order by cp.time_publication desc")
    List<ClsPrescription> getPrescriptionsByTypeRequestIds(Set<Long> typeRequestIds, Long orgId, Integer status);


    @Query(nativeQuery = true, value = "" +
            "select * " +
            "from cls_prescription cp " +
            "   join reg_organization_prescription rop on rop.id_prescription = cp.id " +
            "where rop.id_organization = :orgId")
    List<ClsPrescription> getPrescriptionsByOrganizationId(Long orgId);

    @Query(nativeQuery = true, value = "" +
            "select * " +
            "from cls_prescription " +
            "where id_type_request = :idTypeRequest " +
            "   and status = :status " +
            "order by time_publication")
    List<ClsPrescription> getPrescriptionsByTypeRequestId(Long idTypeRequest, Integer status);
}
