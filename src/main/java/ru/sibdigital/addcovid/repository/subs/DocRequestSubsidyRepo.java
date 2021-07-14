package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;

import java.util.List;
import java.util.Optional;

public interface DocRequestSubsidyRepo extends JpaRepository<DocRequestSubsidy, Long> {
    Optional<List<DocRequestSubsidy>> findAllByOrganizationIdAndStatusActivity(Long idOrganization, Integer statusValue);
    Optional<List<DocRequestSubsidy>> findAllByOrganizationIdAndStatusActivityAndIsDeleted(Long idOrganization, Integer statusValue, Boolean isDeleted);

}
