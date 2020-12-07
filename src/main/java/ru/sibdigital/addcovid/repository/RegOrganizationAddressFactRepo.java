package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationAddressFact;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RegOrganizationAddressFactRepo extends JpaRepository<RegOrganizationAddressFact, Long> {

    @Query(nativeQuery = true, value = "select * from reg_organization_address_fact where id = :id")
    Optional<RegOrganizationAddressFact> findById(Long id);

    @Query(nativeQuery = true, value = "select * from reg_organization_address_fact where id_organization = :id_organization")
    List<Map<String, Object>> findByIdOrganization(@Param("id_organization") Integer id_organization);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into reg_organization_address_fact (id_organization, id_request, is_deleted, time_create, fias_objectguid, fias_region_objectguid, fias_raion_objectguid, full_address, is_hand)\n" +
            "VALUES (:id_organization, null, false, default, :fias_objectguid, :fias_region_objectguid, :fias_raion_objectguid, :full_address, false)")
    public void insertOrg(
            @Param("id_organization") Integer id_organization,
            //Long id_request,
            String fias_objectguid,
            String fias_region_objectguid,
            String fias_raion_objectguid,
            String full_address
    );

    @Query(nativeQuery = true, value = "select id, id_request,full_address, fias_objectguid, fias_region_objectguid, fias_raion_objectguid from reg_organization_address_fact where id_organization = :id_organization")
    public Optional<List<Map<String, Object>>> findByOrganizationId(Long id_organization);

    @Modifying
    @Query(nativeQuery = true, value = "delete from reg_organization_address_fact where id=:id")
    public void customDeleteById(@Param("id") Long id);

}
