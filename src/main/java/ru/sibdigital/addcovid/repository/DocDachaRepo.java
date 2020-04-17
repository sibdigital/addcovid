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
    
    
    @Query(nativeQuery = true, value = "SELECT date_trunc('day',dd.time_create) as time_create, date_trunc('day',dd.valid_date) as valid_date, count(dd.valid_date)" +
            "FROM (" +
            "    SELECT date_trunc('day',time_create) as time_create, COUNT(time_create) as timecr" +
            "    FROM doc_dacha" +
            "    WHERE date_trunc('day',time_create)  >=  date_trunc('day',CURRENT_DATE - 6)" +
            "    group by date_trunc('day',time_create)" +
            "    ) as per" +
            "        left join doc_dacha as dd" +
            "            on date_trunc('day',per.time_create) = date_trunc('day',dd.time_create)" +
            "WHERE date_trunc('day',dd.valid_date)  <>  date_trunc('day',CURRENT_DATE)" +
            "group by date_trunc('day',dd.time_create), date_trunc('day',dd.valid_date)" +
            "order by date_trunc('day',dd.time_create), date_trunc('day',dd.valid_date)")
    List<Map<String, Object>> getStatisticForNearestDays();

    @Query(nativeQuery = true, value = "select dd.status_review, count(dd.status_review) from doc_dacha as dd group by dd.status_review order by dd.status_review")
    List<Map<String, Object>> getCountByReviewStatus();

//    @Query(nativeQuery = true, value = "SELECT date_trunc('day',dd.time_create) as time_create, count(dd.time_create) " +
//            "FROM doc_dacha as dd " +
//            "group by date_trunc('day',dd.time_create) " +
//            "order by date_trunc('day',dd.time_create)")
//    List<Map<String, Object>> getTotalStatisticByTimeCreate();

    @Query(nativeQuery = true, value = "SELECT date_trunc('day',dd.valid_date) as valid_date" +
            ", coalesce(count(dd.valid_date),0) as request" +
            ", coalesce(sum(ppl.count),0) as people " +
            "FROM doc_dacha as dd    " +
            "   left join (        " +
            "       select dp.id_doc_dacha, count(dp.id_doc_dacha) " +
            "       from doc_dacha_person as dp " +
            "       group by dp.id_doc_dacha " +
            "   ) as ppl " +
            "       on ppl.id_doc_dacha = dd.id " +
            "group by date_trunc('day',dd.valid_date)" +
            "order by date_trunc('day',dd.valid_date)")
    List<Map<String, Object>> getTotalStatisticByValidDate();
}
