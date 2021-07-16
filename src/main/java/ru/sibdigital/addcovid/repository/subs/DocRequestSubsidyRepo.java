package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DocRequestSubsidyRepo extends JpaRepository<DocRequestSubsidy, Long> {
    Optional<List<DocRequestSubsidy>> findAllByOrganizationIdAndStatusActivity(Long idOrganization, Integer statusValue);
    Optional<List<DocRequestSubsidy>> findAllByOrganizationIdAndStatusActivityAndIsDeleted(Long idOrganization, Integer statusValue, Boolean isDeleted);


    @Query(value =
            "select count(id) as cnt\n " +
            "from reg_queue_tasks rqt\n " +
            "where rqt.created_at <= :date ",
            nativeQuery = true
    )
    Long getEarilerTasks(Date date);
}
