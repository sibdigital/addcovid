package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;

import java.util.List;
import java.util.Optional;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
    @Query(value = "select max(t.id) from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile is null")
    Optional<Long> findLastSubsidyFile();

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.fileType.id = :file_type_id and t.requestSubsidy.id = :request_subsidy_id and t.requestSubsidyFile is null")
    Optional<List<TpRequestSubsidyFile>> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile.id = :id")
    Optional<TpRequestSubsidyFile> findSignatureFile (Long id);
}
