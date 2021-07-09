package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.ClsSubsidy;

public interface ClsSubsidyRepo extends JpaRepository<ClsSubsidy, Long> {
}
