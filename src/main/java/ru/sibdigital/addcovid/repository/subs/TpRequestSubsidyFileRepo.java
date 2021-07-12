package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
    @Query(value = "select max(t.id) from TpRequestSubsidyFile as t where t.isDeleted = false")
    Long findLastSubsidyFile();
}
