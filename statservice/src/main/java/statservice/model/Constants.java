package statservice.model;

import lombok.experimental.UtilityClass;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
