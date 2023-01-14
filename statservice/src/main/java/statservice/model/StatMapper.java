package statservice.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatMapper {
    private static final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatHits toStatHit(EndpointHit hit) {
        return new StatHits(
                null,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                LocalDateTime.parse(hit.getTimestamp(), formatter));
    }
}
