package ru.sibdigital.addcovid.repository.classifier.gov;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;


import java.util.List;
import java.util.UUID;

@Repository
public interface OkvedRepo extends JpaRepository<Okved, Integer>, JpaSpecificationExecutor<Okved> {
    @Query(value = "SELECT okved.*\n" +
            "FROM okved\n" +
            "WHERE okved.kind_code = :kind_code AND okved.kind_name = :kind_name AND okved.version = :version",
            nativeQuery = true)
    List<Okved> findOkvedByKindCodeAndKindNameAAndVersion(String kind_code, String kind_name, String version);

    @Modifying
    @Query(value = "update okved set status = 0", nativeQuery = true)
    void resetStatus();

    @Modifying
    @Query(value = "update okved set ts_kind_name = to_tsvector('russian',kind_name), " +
            "ts_description = to_tsvector('russian', description)", nativeQuery = true)
    void setTsVectors();

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE okved\nSET ts_kind_name = to_tsvector('russian',kind_name),\n    ts_description = to_tsvector('russian', description)\nWHERE id = :id",
            nativeQuery = true
    )
    void setTsVectorsById(@Param("id") UUID id);

    Okved findByKindCode(String kindCode);

    Okved findByPath(String path);

    @Query(
            value = "select * from okved where kind_code like %:text% or lower(kind_name) like %:text% order by kind_code",
            nativeQuery = true
    )
    List<Okved> findBySearchText(@Param("text") String text);

    @Query(nativeQuery = true,
            value = "select * from okved where version = :version and (kind_code like %:text% or lower(kind_name) like %:text%) order by kind_code")
    Page<Okved> findAllBySearchTextAndVersion(String text, String version, Pageable pageable);

    @Query(
            value = "select * from okved where version = :version order by kind_code",
            nativeQuery = true
    )
    List<Okved> findOkvedsByVersion(@Param("version") String version);

    Okved findOkvedById(@Param("id") UUID id);

    @Query(nativeQuery = true, value = "select * from okved where kind_code = :kind_code and version = :version")
    Okved findByKindCodeAndVersion(String kind_code, String version);

}
