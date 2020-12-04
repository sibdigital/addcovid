package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationAddressFact;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RegOrganizationAddressFactRepo extends JpaRepository<RegOrganizationAddressFact, Long> {

    @Query(nativeQuery = true, value = "select * from reg_organization_address_fact where id = :id")
    Optional<RegOrganizationAddressFact> findById(Long id);

    @Query(nativeQuery = true, value = "select * from reg_organization_address_fact where id_organization = :id_organization")
    List<Map<String, Object>> findByIdOrganization(@Param("id_organization") Integer id_organization);

}
