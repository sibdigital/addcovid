package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;

public interface TpRequiredSubsidyFileRepo extends JpaRepository<TpRequiredSubsidyFile, Long> {
}
