package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegOrganizationOkved;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegOrganizationOkvedRepo extends JpaRepository<RegOrganizationOkved, Long> {
    @Query(nativeQuery = true, value = "select * from reg_organization_okved where (id_organization = :id and is_main = false)")
    Optional<List<RegOrganizationOkved>> findAllByIdOrganizationIsNotMain(Long id);

    @Query(nativeQuery = true, value = "select * from reg_organization_okved where (id_organization = :id and is_main = true)")
    Optional<RegOrganizationOkved> findAllByIdOrganizationIsMain(Long id);

    @Query(nativeQuery = true, value = "select * from reg_organization_okved where (id_organization = :id)")
    Optional<List<RegOrganizationOkved>> findAllByIdOrganization(Long id);
}
