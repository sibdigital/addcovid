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

    @Query(nativeQuery = true, value = "select * from fias.addr_object where level = :level")
    Optional<List<FIASAddrObject>> findByLevel(String level);

    @Query(nativeQuery = true, value = "select id, objectguid, name " +
            " from fias.addr_object where level = :level")
    List<Map<String, Object>> findByL(String level);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectguid, fao.name as value \n" +
            " from fias.addr_object as fao\n" +
            " where level='1'" +
            " order by fao.name")
    List<Map<String, Object>> findRegions();

    @Query(nativeQuery = true, value = "select * " +
            " from fias.addr_object where level = '1' and name = :name")
    Optional<Map<String, Object>> findRegion(String name);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectguid, fao.name as value " +
            " from fias.addr_object as fao where level='4' or level='5' or level='6'" +
            " order by fao.name")
    List<Map<String, Object>> findCities();

    @Query(nativeQuery = true, value = "with sdr as (\n" +
            "    select fao.objectid\n" +
            "    from fias.adm_hierarchy_item as fao\n" +
            "    where fao.parentobjid = :regionObjectId\n" +
            ")\n" +
            "select fao.id, fao.objectguid, fao.name as value\n" +
            "from fias.addr_object as fao\n" +
            "    inner join sdr\n" +
            "        on (fao.objectid) = (sdr.objectid)")
    List<Map<String, Object>> findCities(Long regionObjectId);

    @Query(nativeQuery = true, value = "select * " +
            " from fias.addr_object where level = '5' and name = :name")
    Optional<Map<String, Object>> findCityOrRaion(String name);
}
