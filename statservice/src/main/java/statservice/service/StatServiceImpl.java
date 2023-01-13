package statservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import statservice.model.EndpointHit;
import statservice.model.StatMapper;
import statservice.model.ViewStats;
import statservice.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public void saveHit(EndpointHit hit) {
        statRepository.save(StatMapper.toStatHit(hit));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, String[] uris, boolean unique) {
        if (unique)
            return statRepository.getStatsUnique(LocalDateTime.parse(start, formatter),
                    LocalDateTime.parse(end, formatter), uris);
        else
            return statRepository.getStatsNonUnique(LocalDateTime.parse(start, formatter),
                    LocalDateTime.parse(end, formatter), uris);
    }
}
