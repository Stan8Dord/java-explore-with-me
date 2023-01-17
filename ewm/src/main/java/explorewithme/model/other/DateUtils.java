package explorewithme.model.other;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateUtils {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime getTimeToMinutes() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
}
