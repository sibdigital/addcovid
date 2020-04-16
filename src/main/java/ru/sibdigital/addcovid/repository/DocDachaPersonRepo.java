package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.sibdigital.addcovid.model.DocDachaPerson;

public interface DocDachaPersonRepo extends CrudRepository<DocDachaPerson, Long> {
    @Query(nativeQuery = true, value = "SELECT count(*) FROM ( SELECT DISTINCT firstname, lastname, patronymic FROM doc_dacha_person WHERE id_doc_dacha IN (SELECT id FROM doc_dacha WHERE status_review = :status)) AS s;")
    Long getTotalApprovedPeopleByReviewStatus(@Param("status") int status);
}
