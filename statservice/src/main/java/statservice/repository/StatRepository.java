package statservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import statservice.model.StatHits;
import statservice.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatHits, Long> {
    @Query("select new statservice.model.ViewStats(sh.app, sh.uri, count(sh.id)) " +
            "from StatHits as sh " +
            "where (sh.timestamp between ?1 and ?2) " +
            "and sh.uri in ?3 " +
            "group by sh.app, sh.uri")
    List<ViewStats> getStatsNonUnique(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select new statservice.model.ViewStats(sh.app, sh.uri, count(distinct sh.ip)) " +
            "from StatHits as sh " +
            "where (sh.timestamp between ?1 and ?2) " +
            "and sh.uri in ?3 " +
            "group by sh.app, sh.uri")
    List<ViewStats> getStatsUnique(LocalDateTime start, LocalDateTime end, String[] uris);
}
