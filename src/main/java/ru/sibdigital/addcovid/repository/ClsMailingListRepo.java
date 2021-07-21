package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsMailingList;

import java.util.List;

@Repository
public interface ClsMailingListRepo extends JpaRepository<ClsMailingList, Long> {

    @Query(value = "SELECT cml1.*\n" +
            "FROM cls_mailing_list cml1\n" +
            "WHERE id = 1\n" +
            "UNION\n" +
            "SELECT cml2.*\n" +
            "FROM cls_mailing_list cml2\n" +
            "INNER JOIN reg_mailing_list_follower rmlf on cml2.id = rmlf.id_mailing_list\n" +
            "    AND rmlf.id_principal =:id_principal\n" +
            "    AND cml2.status = 1\n" +
            "    AND rmlf.deactivation_date IS NULL", nativeQuery = true)
    List<ClsMailingList> findMyMailingList(Long id_principal);

    @Query(value = "SELECT cml.*, rmlf.id\n" +
            "FROM cls_mailing_list cml\n" +
            "LEFT JOIN reg_mailing_list_follower rmlf on cml.id = rmlf.id_mailing_list\n" +
            "    AND rmlf.id_principal =:id_principal\n" +
            "    AND rmlf.deactivation_date IS NULL\n" +
            "WHERE cml.status = 1 AND rmlf.id IS NULL", nativeQuery = true)
    List<ClsMailingList> findAvailableMailingListNotMine(Long id_principal);

}