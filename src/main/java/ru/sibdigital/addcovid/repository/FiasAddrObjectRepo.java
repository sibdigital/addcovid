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

    @Query(nativeQuery = true, value = "select id, objectid, name as value, typename, level \n" +
            "from fias.addr_object \n" +
            "where objectid = :objectId")
    Map<String, Object> findByObjectId(@Param("objectId") Long objectId);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectid, fao.name as value, fao.typename, fahi.regioncode\n" +
            " from fias.addr_object as fao inner join fias.adm_hierarchy_item as fahi\n" +
            " on fao.objectid = fahi.objectid\n" +
            " and level=1\n" +
            " order by fao.name")
    List<Map<String, Object>> findRegions();

    @Query(nativeQuery = true, value = "with cit as (\n" +
            "    select fahi.objectid\n" +
            "    from fias.adm_hierarchy_item as fahi\n" +
            "    where fahi.parentobjid = :regionObjectId" +
            ")\n" +
            "select fao.id, fao.objectid, fao.name as value, fao.typename, fao.level \n" +
            " from cit inner join fias.addr_object as fao\n" +
            " on (fao.objectid) = (cit.objectid) " +
            " and fao.level between 2 and 6 " +
            " order by fao.level DESC, fao.name " +
            " ")
    List<Map<String, Object>> findRaions(@Param("regionObjectId") Long regionObjectId);

    @Query(nativeQuery = true, value = "select fao.id, fao.objectid, fao.name as value, fao.typename " +
            " from fias.addr_object as fao where level between 4 and 6" +
            " order by fao.name")
    List<Map<String, Object>> findCities();

    @Query(nativeQuery = true, value = "with fa as (\n" +
            "    select objectid as dff, parentobjid, regioncode, areacode, citycode\n" +
            "    from fias.adm_hierarchy_item as fais\n" +
            "    where regioncode = :regionCode and plancode = '0' and streetcode = '0'\n" +
            "    limit 100\n" +
            ")\n" +
            " select fao.id, fao.objectid, fao.name as value, fao.typename, fao.level, fao2.objectid as districtObjectId, fao2.name as districtname, fao2.typename as districttypename, fa.areacode, fa.citycode\n" +
            " from fias.addr_object as fao inner join fa\n" +
            "                                        on (fa.dff) = (fao.objectid) and level between 4 and 6\n" +
            "                             inner join fias.addr_object as fao2\n" +
            "                                        on (fa.parentobjid) = (fao2.objectid)\n" +
            " ORDER BY fao.name")
    List<Map<String, Object>> findCities(@Param("regionCode") Short regionCode);

    @Query(nativeQuery = true, value = "with nd as (\n" +
            "    select id, objectid, name as value, typename, level\n" +
            "    from fias.addr_object as fao\n" +
            "    where level between 4 and 6\n" +
            "      and name ~* (:objectName)\n" +
            ")\n" +
            " select nd.*, fao.objectid as districtObjectId, fao.name as districtname, fao.typename as districttypename, fahi.areacode, fahi.citycode\n" +
            " from fias.adm_hierarchy_item as fahi\n" +
            "    inner join nd\n" +
            "        on (nd.objectid) = (fahi.objectid) and fahi.regioncode=:regionCode and plancode = '0' and streetcode = '0'\n" +
            "    inner join fias.addr_object as fao\n" +
            "        on (fahi.parentobjid) = (fao.objectid)\n" +
            " order by nd.value\n" +
            " limit 50")
    List<Map<String, Object>> findCitiesWithFilter(@Param("regionCode") Short regionCode, String objectName);

    @Query(nativeQuery = true, value = "with fa as (\n" +
            "    select objectid as dff, parentobjid, regioncode, areacode, citycode\n" +
            "    from fias.adm_hierarchy_item as fais\n" +
            "    where objectid = :objectId\n" +
            ")\n" +
            " select fao.id, fao.objectid, fao.name as value, fao.typename, fao.level, fao2.objectid as districtObjectId, fao2.name as districtname, fao2.typename as districttypename, fa.areacode, fa.citycode\n" +
            " from fias.addr_object as fao inner join fa\n" +
            "                                        on (fa.dff) = (fao.objectid) inner join fias.addr_object as fao2\n" +
            "                                                               on (fa.parentobjid) = (fao2.objectid)")
    Map<String, Object> findCityByObjectId(@Param("objectId") Long objectId);

    @Query(nativeQuery = true, value = "with cit as (\n" +
            "         select fahi.objectid\n" +
            "         from fias.adm_hierarchy_item as fahi\n" +
            "         where fahi.parentobjid = :raionOrCityObjectId \n" +
            "     )\n" +
            "select fao.id, fao.objectid, fao.name as value, fao.typename\n" +
            " from fias.addr_object as fao inner join cit\n" +
            "                                        on (fao.objectid) = (cit.objectid)" +
            " order by fao.name")
    List<Map<String, Object>> findStreetsByRaionOrCity(@Param("raionOrCityObjectId") Long raionOrCityObjectId);

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
