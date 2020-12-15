package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
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

    @Query(nativeQuery = true, value = "select id, objectid, name as value, typename, level \n" +
            "from fias.addr_object \n" +
            "where objectid = :objectId")
    Map<String, Object> findByObjectId(@Param("objectId") Long objectId);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectid, fao.name as value, fao.typename \n" +
            " from fias.addr_object as fao\n" +
            " where level='1'" +
            " order by fao.name")
    List<Map<String, Object>> findRegions();

    @Query(nativeQuery = true, value = "with sd as (\n" +
            "    select objectid\n" +
            "    from fias.addr_object as fao\n" +
            "    where fao.objectid = :regionGuid\n" +
            "),\n" +
            "cit as (\n" +
            "    select fahi.objectid\n" +
            "    from fias.adm_hierarchy_item as fahi\n" +
            "             inner join sd\n" +
            "                        on (sd.objectid) = (fahi.parentobjid)\n" +
            ")\n" +
            "select fao.id, fao.objectid, fao.name as value, fao.typename, fao.level \n" +
            " from fias.addr_object as fao inner join cit\n" +
            " on (fao.objectid) = (cit.objectid) " +
            " where fao.level='2' or fao.level='3' or level='4' or level='5' or level='6'" +
            " order by fao.level DESC, fao.name " +
            " ")
    List<Map<String, Object>> findRaions(@Param("regionGuid") Long regionObjectId);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectid, fao.name as value, fao.typename " +
            " from fias.addr_object as fao where level='4' or level='5' or level='6'" +
            " order by fao.name")
    List<Map<String, Object>> findCities();

    @Query(nativeQuery = true, value = "with sd as (\n" +
            "    select objectid\n" +
            "    from fias.addr_object as fao\n" +
            "    where fao.objectid = :regionGuid\n" +
            "),\n" +
            "cit as (\n" +
            "    select fahi.objectid\n" +
            "    from fias.adm_hierarchy_item as fahi\n" +
            "             inner join sd\n" +
            "                        on (sd.objectid) = (fahi.parentobjid)\n" +
            ")\n" +
            "select fao.id, fao.objectid, fao.name as value, fao.typename, fao.level \n" +
            " from fias.addr_object as fao inner join cit\n" +
            " on (fao.objectid) = (cit.objectid) " +
            " where fao.level='4' or fao.level='5' or fao.level='6' " +
            " order by fao.name")
    List<Map<String, Object>> findCities(@Param("regionGuid") Long regionObjectId);

    @Query(nativeQuery = true, value = "select id, objectid, name as value, typename, level \n" +
            "from fias.addr_object \n" +
            "where objectid = :objectId")
    Map<String, Object> findCityByObjectId(@Param("objectId") Long objectId);

    @Query(nativeQuery = true, value = "with sd as (\n" +
            "    select objectid\n" +
            "    from fias.addr_object as fao\n" +
            "    where fao.objectid = :raionObjectId\n" +
            "),\n" +
            "     cit as (\n" +
            "         select fahi.objectid\n" +
            "         from fias.adm_hierarchy_item as fahi\n" +
            "                  inner join sd\n" +
            "                             on (sd.objectid) = (fahi.parentobjid)\n" +
            "     )\n" +
            "select fao.id, fao.objectid, fao.name as value, fao.typename\n" +
            " from fias.addr_object as fao inner join cit\n" +
            "                                        on (fao.objectid) = (cit.objectid)" +
            " order by fao.name")
    List<Map<String, Object>> findStreetsByRaionOrCity(@Param("raionObjectId") Long raionObjectId);

    @Query(nativeQuery = true, value = "with sd as (\n" +
            "    select objectid\n" +
            "    from fias.addr_object as fao\n" +
            "    where fao.objectid = :streetObjectId\n" +
            "),\n" +
            "     cit as (\n" +
            "         select fahi.objectid\n" +
            "         from fias.adm_hierarchy_item as fahi\n" +
            "                  inner join sd\n" +
            "                             on (sd.objectid) = (fahi.parentobjid)\n" +
            "     )\n" +
            "select fh.objectid, fh.housenum as value, fao.typename \n" +
            " from fias.house as fh inner join cit\n" +
            "    on (fh.objectid) = (cit.objectid)" +
            "  order by fh.housenum")
    List<Map<String, Object>> findHouseByStreet(@Param("streetObjectId") Long streetObjectId);
}
