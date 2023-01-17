package explorewithme.model.event;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.other.Location;
import explorewithme.model.user.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private String description;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state;
    private String title;
    private Long views;
}
