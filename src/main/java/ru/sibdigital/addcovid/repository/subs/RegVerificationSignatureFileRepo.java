package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.ClsPrincipal;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;

import java.util.List;
import java.util.Optional;

public interface RegVerificationSignatureFileRepo extends JpaRepository<RegVerificationSignatureFile, Long> {
    @Query(value = "select t from RegVerificationSignatureFile as t " +
            "where t.isDeleted = false " +
            "and t.requestSubsidy.id = :idRequest " +
            "and t.principal = :principal")
    Optional<List<RegVerificationSignatureFile>> findByIdRequestAndIdPrincipal (Long idRequest, ClsPrincipal principal);

    @Query(value = "select t from RegVerificationSignatureFile as t " +
            "where t.isDeleted = false " +
            "and t.requestSubsidyFile.id = :idFile " +
            "and t.requestSubsidySubsidySignatureFile.id = :idSignature")
    Optional<RegVerificationSignatureFile> findByIdFileAndIdSignature (Long idFile, Long idSignature);

    RegVerificationSignatureFile findByRequestSubsidy_IdAndRequestSubsidyFile_IdAndRequestSubsidySubsidySignatureFile_Id
            (Long idRequestSubsidy, Long idRequestSubsidyFile, Long idRequestSubsidySubsidySignatureFile);
}
