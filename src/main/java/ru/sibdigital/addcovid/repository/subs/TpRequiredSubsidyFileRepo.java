package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;

import java.util.List;
import java.util.Optional;

public interface TpRequiredSubsidyFileRepo extends JpaRepository<TpRequiredSubsidyFile, Long> {
    @Query(value = "select t from TpRequiredSubsidyFile as t where t.idSubsidy = :id and t.isDeleted = false order by t.isRequired desc")
    Optional<List<TpRequiredSubsidyFile>> findAllBySubsidyIdAndIsDeletedFalse(Long id);

}
