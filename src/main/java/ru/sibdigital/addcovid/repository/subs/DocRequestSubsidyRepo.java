package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;

public interface DocRequestSubsidyRepo extends JpaRepository<DocRequestSubsidy, Long> {
}
