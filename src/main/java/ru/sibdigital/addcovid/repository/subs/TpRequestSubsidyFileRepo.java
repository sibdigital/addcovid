package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;

import java.util.List;
import java.util.Optional;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
    @Query(value = "select t from TpRequestSubsidyFile as t " +
            "where t.isDeleted = false and t.fileType.id = :file_type_id and t.requestSubsidy.id = :request_subsidy_id and t.requestSubsidyFile is null")
    Optional<List<TpRequestSubsidyFile>> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile.id = :id")
    Optional<TpRequestSubsidyFile> findSignatureFile (Long id);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.requestSubsidyFile is not null and t.isDeleted = false and t.requestSubsidy.id = :idRequest")
    Optional<List<TpRequestSubsidyFile>> getSignatureFilesByIdRequest(Long idRequest);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile.id = :id")
    Optional<TpRequestSubsidyFile> getSignatureFileByDocFileId(Long id);
    
    @Query(value = "select t from TpRequestSubsidyFile as t where t.id = :id and t.isDeleted = false")
    Optional<TpRequestSubsidyFile> getDocFilesBySignature(Long id);
}
