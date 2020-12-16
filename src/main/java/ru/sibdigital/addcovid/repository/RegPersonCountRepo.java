package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegPersonCount;

@Repository
public interface RegPersonCountRepo extends JpaRepository<RegPersonCount, Long> {

    @Query(value = "SELECT *\n" +
            "FROM reg_person_count\n" +
            "WHERE id_organization = :id_organization\n" +
            "ORDER BY time_edit DESC\n" +
            "LIMIT 1;", nativeQuery = true)
    RegPersonCount getLastPersonCntByOrganization_Id(Long id_organization);
}
