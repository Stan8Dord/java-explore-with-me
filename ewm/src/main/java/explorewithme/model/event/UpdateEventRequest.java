package explorewithme.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UpdateEventRequest {
    @NotNull
    private long eventId;
    private String annotation;
    private String description;
    private Long category;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private String title;
}
