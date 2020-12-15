package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {

    @Query(nativeQuery = true, value = "select * " +
            "from cls_organization " +
            "where inn = :inn and id_principal is not null " +
            "   and not is_deleted " +
            "   and is_activated")
    ClsOrganization findByInnAndPrincipalIsNotNull(String inn);

    @Query(nativeQuery = true, value = "select * " +
            "from cls_organization " +
            "where inn = :inn and hash_code = :hashCode " +
            "   and not is_deleted")
    ClsOrganization findByInnAndHashCode(String inn, String hashCode);
}
