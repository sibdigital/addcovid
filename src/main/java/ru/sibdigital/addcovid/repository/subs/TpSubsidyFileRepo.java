package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.TpSubsidyFile;

public interface TpSubsidyFileRepo extends JpaRepository<TpSubsidyFile, Long> {
}
