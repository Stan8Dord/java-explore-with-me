package explorewithme.model.event;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.user.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Long views;
}
