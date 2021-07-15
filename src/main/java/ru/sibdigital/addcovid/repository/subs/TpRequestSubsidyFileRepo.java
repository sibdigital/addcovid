package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TpRequestSubsidyFileRepo extends JpaRepository<TpRequestSubsidyFile, Long> {
    @Query(value = "select max(t.id) from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile is null")
    Optional<Long> findLastSubsidyFile();

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.fileType.id = :file_type_id and t.requestSubsidy.id = :request_subsidy_id and t.requestSubsidyFile is null")
    Optional<List<TpRequestSubsidyFile>> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.isDeleted = false and t.requestSubsidyFile.id = :id")
    Optional<TpRequestSubsidyFile> findSignatureFile (Long id);

    @Query(value = "select t from TpRequestSubsidyFile as t where t.requestSubsidyFile is not null and t.isDeleted = false")
    Optional<List<TpRequestSubsidyFile>> getSignatureFiles();

    @Query(value = "select t from TpRequestSubsidyFile as t where t.id = :id and t.isDeleted = false")
    Optional<TpRequestSubsidyFile> getDocFilesBySignature(Long id);


    @Query(
            value = "with idFiles as (\n" +
                    "    select *\n" +
                    "    from subs.tp_request_subsidy_file\n" +
                    "    where id_request = :id_request_subsidy\n" +
                    "      and is_signature = false\n" +
                    ")\n" +
                    "select idFiles.id, idFiles.id_request, idFiles.id_organization, idFiles.id_department, idFiles.id_processed_user, idFiles.id_file_type, idFiles.is_deleted, idFiles.time_create, idFiles.attachment_path, idFiles.file_name, idFiles.view_file_name, idFiles.original_file_name, idFiles.file_extension, idFiles.hash, idFiles.file_size, trsf.is_signature, idFiles.id_subsidy_request_file\n" +
                    "from idFiles\n" +
                    "    left join subs.tp_request_subsidy_file as trsf\n" +
                    "        on (idFiles.id) = (trsf.id_subsidy_request_file) and trsf.is_signature = true\n",
            nativeQuery = true
    )
    public List<TpRequestSubsidyFile> getTpRequestSubsidyFilesByDocRequestId(Long id_request_subsidy);


    @Query(
            value = "with idFiles as (\n" +
                    "    select *\n" +
                    "    from subs.tp_request_subsidy_file\n" +
                    "    where id_request = :id_request_subsidy\n" +
                    "      and is_signature = false\n" +
                    ")\n" +
                    "select rvsf.id_request_subsidy_file, rvsf.verify_status, rvsf.verify_result\n" +
                    "from idFiles\n" +
                    "         inner join  subs.tp_request_subsidy_file as trsf\n" +
                    "                     on (idFiles.id) = (trsf.id_subsidy_request_file) and trsf.is_signature = true\n" +
                    "    inner join subs.reg_verification_signature_file as rvsf\n" +
                    "        on (idFiles.id_request, idFiles.id, trsf.id) = (rvsf.id_request, rvsf.id_request_subsidy_file, rvsf.id_request_subsidy_signature_file)",
            nativeQuery = true
    )
    public List<Map<String, String>> getSignatureVerificationTpRequestSubsidyFile(Long id_request_subsidy);
}
