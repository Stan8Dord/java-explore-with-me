package explorewithme.service;

import explorewithme.model.event.*;
import explorewithme.model.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable, String sort,
                                  int from, int size, String ip, String uri);
    EventFullDto getEvent(long eventId, String ip, String uri);

    List<EventShortDto> getEventsByUser(long userId, int from, int size);

    EventFullDto getEventByUser(long userId, long eventId);

    EventFullDto addNewEvent(long userId, NewEventDto eventDto);

    EventFullDto modifyEvent(long userId, UpdateEventRequest eventDto);

    EventFullDto cancelEvent(long userId, long eventId);

    List<EventFullDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                        String rangeStart, String rangeEnd, int from, int size);

    EventFullDto modifyEventByAdmin(long eventId, AdminUpdateEventRequest modEvent);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);

    ParticipationRequestDto confirmEventRequest(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectEventRequest(long userId, long eventId, long reqId);

    ParticipationRequestDto cancelRequest(long userId, long reqId);
}
