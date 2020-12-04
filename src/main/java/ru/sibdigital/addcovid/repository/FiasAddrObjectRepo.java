package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocAddressFact;
import ru.sibdigital.addcovid.model.FIASAddrObject;
import ru.sibdigital.addcovid.model.RegOrganizationAddressFact;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FiasAddrObjectRepo extends JpaRepository<FIASAddrObject, Long> {

    @Query(nativeQuery = true, value = "set search_path = \"fias\";\n" +
            "select *\n" +
            "from addr_object as ao\n" +
            "where ao.level='1'\n" +
            "\n")
    List<Map<String, Object>> findRegions();

    @Query(nativeQuery = true, value = "set search_path = \"fias\";\n" +
            "select *\n" +
            "from addr_object as ao\n" +
            "where ao.level='4' or ao.level='5' or ao.level='6';\n")
    List<Map<String, Object>> findCities();
}
