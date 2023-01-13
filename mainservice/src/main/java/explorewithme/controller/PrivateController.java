package explorewithme.controller;

import explorewithme.model.event.EventFullDto;
import explorewithme.model.event.EventShortDto;
import explorewithme.model.event.NewEventDto;
import explorewithme.model.event.UpdateEventRequest;
import explorewithme.model.request.ParticipationRequestDto;
import explorewithme.service.EventService;
import explorewithme.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public PrivateController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByUser(@PathVariable("userId") long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable("userId") long userId,
                                            @PathVariable("eventId")long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsByUser(@PathVariable("userId") long userId,
                                                                @PathVariable("eventId") long eventId) {
        return requestService.getEventRequestsByUser(userId, eventId);
    }

    @PostMapping("/events")
    public EventFullDto addNewEvent(@PathVariable("userId") long userId, @RequestBody NewEventDto eventDto) {
        return eventService.addNewEvent(userId, eventDto);
    }

    @PatchMapping("/events")
    public EventFullDto modifyEvent(@PathVariable("userId") long userId, @RequestBody UpdateEventRequest eventDto) {
        return eventService.modifyEvent(userId, eventDto);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId) {
        return eventService.cancelEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmEventRequest(@PathVariable("userId") long userId,
                                   @PathVariable("eventId") long eventId,
                                   @PathVariable("reqId") long reqId) {
        return eventService.confirmEventRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectEventRequest(@PathVariable("userId") long userId,
                                              @PathVariable("eventId") long eventId,
                                              @PathVariable("reqId") long reqId) {
        return eventService.rejectEventRequest(userId, eventId, reqId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto addNewRequest(@PathVariable("userId") long userId,
                                                 @RequestParam long eventId) {
        return requestService.addNewRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") long userId,
                                              @PathVariable("requestId") long reqId) {
        return eventService.cancelRequest(userId, reqId);
    }
}
