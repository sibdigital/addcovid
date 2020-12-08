package ru.sibdigital.addcovid.repository.classifier.gov;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgripOkved;

import java.util.List;


@Repository
public interface RegEgripOkvedRepo extends JpaRepository<RegEgripOkved, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_egrip_okved\n" +
            "WHERE id_egrip = :id_egrip",
            nativeQuery = true)
    void deleteRegEgripOkved(@Param("id_egrip") Long id_egrip);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reg_egrip_okved\n" +
            "WHERE id_egrip in (:id_egrips)",
            nativeQuery = true)
    void deleteRegEgrulOkveds(@Param("id_egrips") List<Long> id_egrips);

}
