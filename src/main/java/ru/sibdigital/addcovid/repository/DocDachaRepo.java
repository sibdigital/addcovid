package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocDacha;

import java.util.List;
import java.util.Map;

@Repository
public interface DocDachaRepo extends JpaRepository<DocDacha, Long> {

    @Query(nativeQuery = true, value = "SELECT date_trunc('day',doc_dacha.time_create) as date, COUNT(*) AS total FROM doc_dacha GROUP BY date_trunc('day',doc_dacha.time_create) ORDER BY date_trunc('day',doc_dacha.time_create);")
    public List<Map<String, Object>> getStatisticForEachDay();

}
