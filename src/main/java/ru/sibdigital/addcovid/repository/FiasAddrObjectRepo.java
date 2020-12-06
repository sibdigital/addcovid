package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.FiasAddrObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FiasAddrObjectRepo extends JpaRepository<FiasAddrObject, Long> {

    @Query(nativeQuery = true, value = "select * from fias.addr_object where level = :level")
    Optional<List<FiasAddrObject>> findByLevel(String level);

    @Query(nativeQuery = true, value = "select id, objectguid, name " +
            " from fias.addr_object where level = :level")
    List<Map<String, Object>> findByL(String level);

    @Query(nativeQuery = true, value = "select id, objectguid, name as value " +
            " from fias.addr_object where level = '1'")
    List<Map<String, Object>> findRegions();

    @Query(nativeQuery = true, value = "select * " +
            " from fias.addr_object where level = '1' and name = :name")
    Optional<Map<String, Object>> findRegion(String name);

    @Query(nativeQuery = true, value = "select id, objectguid, name as value " +
            " from fias.addr_object where level = '5'")
    List<Map<String, Object>> findCities();

    @Query(nativeQuery = true, value = "select * " +
            " from fias.addr_object where level = '5' and name = :name")
    Optional<Map<String, Object>> findCityOrRaion(String name);
}
