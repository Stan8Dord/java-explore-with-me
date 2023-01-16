package statservice.model;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class StatMapper {
    public static StatHits toStatHit(EndpointHit hit) {
        return new StatHits(
                null,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                LocalDateTime.parse(hit.getTimestamp(), Constants.formatter));
    }
}
