package explorewithme.service.event;

import explorewithme.model.event.*;
import explorewithme.model.event.dto.*;
import explorewithme.model.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable, String sort,
                                  int from, int size, String ip, String uri, HttpServletRequest request);

    EventFullDto getEvent(long eventId, String ip, String uri, HttpServletRequest request);

    List<EventShortDto> getEventsByUser(long userId, int from, int size);

    EventFullDto getEventByUser(long userId, long eventId, HttpServletRequest request);

    EventFullDto addNewEvent(long userId, NewEventDto eventDto, HttpServletRequest request);

    EventFullDto modifyEvent(long userId, UpdateEventRequest eventDto, HttpServletRequest request);

    EventFullDto cancelEvent(long userId, long eventId, HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                        String rangeStart, String rangeEnd, int from, int size);

    EventFullDto modifyEventByAdmin(long eventId, AdminUpdateEventRequest modEvent, HttpServletRequest request);

    EventFullDto publishEvent(long eventId, HttpServletRequest request);

    EventFullDto rejectEvent(long eventId, HttpServletRequest request);

    ParticipationRequestDto confirmEventRequest(long userId, long eventId, long reqId, HttpServletRequest request);

    ParticipationRequestDto rejectEventRequest(long userId, long eventId, long reqId, HttpServletRequest request);

    ParticipationRequestDto cancelRequest(long userId, long reqId, HttpServletRequest request);

    Event checkEvent(long eventId, HttpServletRequest request);
}
