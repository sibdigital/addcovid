package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegOrganizationAddressFact;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RegOrganizationAddressFactRepo extends JpaRepository<RegOrganizationAddressFact, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into reg_organization_address_fact (id_organization, fias_objectguid, fias_region_objectguid, fias_raion_objectguid, full_address)\n" +
            "values (:id_organization, ':fias_objectguid', ':fias_region_objectguid', ':fias_raion_objectguid', ':full_address');")
    public void insertOrg(
            Integer id_organization,
            //Long id_request,
            String fias_objectguid,
            String fias_region_objectguid,
            String fias_raion_objectguid,
            String full_address
    );

    @Query(nativeQuery = true, value = "select id, id_request,full_address, fias_objectguid, fias_region_objectguid, fias_raion_objectguid from reg_organization_address_fact where id_organization = :id_organization")
    public Optional<List<Map<String, Object>>> findByOrganizationId(Long id_organization);
}
