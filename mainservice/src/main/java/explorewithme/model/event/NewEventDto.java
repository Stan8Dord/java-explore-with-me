package explorewithme.model.event;

import explorewithme.model.other.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewEventDto {
    @NotNull
    private String annotation;
    @NotNull
    private String description;
    @NotNull
    private long category;
    @NotNull
    private String eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @NotNull
    private String title;
}
