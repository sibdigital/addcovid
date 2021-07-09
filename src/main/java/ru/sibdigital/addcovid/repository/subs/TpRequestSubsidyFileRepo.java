package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
}
