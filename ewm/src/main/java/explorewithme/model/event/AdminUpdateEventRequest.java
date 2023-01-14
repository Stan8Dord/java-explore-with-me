package explorewithme.model.event;

import explorewithme.model.other.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUpdateEventRequest {
    private String annotation;
    private String description;
    private Long category;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
