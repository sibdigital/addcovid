package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;

import java.util.List;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long>, JpaSpecificationExecutor<ClsOrganization> {

    @Query(nativeQuery = true, value = "select * " +
            "from cls_organization " +
            "where inn = :inn and id_principal is not null " +
            "   and id_type_organization in (:typeOrganizations) " +
            "   and not is_deleted " +
            "   and is_activated")
    ClsOrganization findByInnAndPrincipalIsNotNull(String inn, List<Integer> typeOrganizations);

    @Query(nativeQuery = true, value = "select * " +
            "from cls_organization " +
            "where email = :email and id_principal is not null " +
            "   and id_type_organization in (:typeOrganizations) " +
            "   and not is_deleted " +
            "   and is_activated")
    ClsOrganization findByEmailAndPrincipalIsNotNull(String email, List<Integer> typeOrganizations);

    @Query(nativeQuery = true, value = "select * " +
            "from cls_organization " +
            "where inn = :inn and hash_code = :hashCode " +
            "   and not is_deleted")
    ClsOrganization findByInnAndHashCode(String inn, String hashCode);
}
