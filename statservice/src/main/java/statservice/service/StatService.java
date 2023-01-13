package statservice.service;

import statservice.model.EndpointHit;
import statservice.model.ViewStats;

import java.util.List;

public interface StatService {
    void saveHit(EndpointHit hit);

    List<ViewStats> getStats(String start, String end, String[] uris, boolean unique);
}
