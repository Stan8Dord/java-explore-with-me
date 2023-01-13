package explorewithme.model.event;

import explorewithme.model.category.Category;
import explorewithme.model.category.CategoryMapper;
import explorewithme.model.other.Location;
import explorewithme.model.user.User;
import explorewithme.model.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(formatter),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews());
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                event.getDescription(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(formatter),
                event.getEventDate().format(formatter),
                UserMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(formatter),
                event.isRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                event.getViews());
    }

    public static Event toEvent(NewEventDto newEvent, User user, Category category) {
        return new Event(
                null,
                newEvent.getAnnotation(),
                newEvent.getDescription(),
                category,
                0,
                LocalDateTime.now(),
                LocalDateTime.parse(newEvent.getEventDate(), formatter),
                user,
                newEvent.getLocation().getLat(),
                newEvent.getLocation().getLon(),
                newEvent.isPaid(),
                newEvent.getParticipantLimit(),
                LocalDateTime.now(),
                newEvent.isRequestModeration(),
                EventState.PENDING,
                newEvent.getTitle(),
                0);
    }
}
