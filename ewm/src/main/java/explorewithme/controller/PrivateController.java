package explorewithme.controller;

import explorewithme.model.event.EventFullDto;
import explorewithme.model.event.EventShortDto;
import explorewithme.model.event.NewEventDto;
import explorewithme.model.event.UpdateEventRequest;
import explorewithme.model.request.ParticipationRequestDto;
import explorewithme.service.EventService;
import explorewithme.service.RequestService;
import explorewithme.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final UserService userService;

    @Autowired
    public PrivateController(EventService eventService, RequestService requestService, UserService userService) {
        this.eventService = eventService;
        this.requestService = requestService;
        this.userService = userService;
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByUser(@PathVariable("userId") long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable("userId") long userId,
                                            @PathVariable("eventId")long eventId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.getEventByUser(userId, eventId, request);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsByUser(@PathVariable("userId") long userId,
                                                                @PathVariable("eventId") long eventId,
                                                                HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return requestService.getEventRequestsByUser(userId, eventId);
    }

    @PostMapping("/events")
    public EventFullDto addNewEvent(@PathVariable("userId") long userId, @RequestBody NewEventDto eventDto,
                                    HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.addNewEvent(userId, eventDto, request);
    }

    @PatchMapping("/events")
    public EventFullDto modifyEvent(@PathVariable("userId") long userId, @RequestBody UpdateEventRequest eventDto,
                                    HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.modifyEvent(userId, eventDto, request);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId,
                                    HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.cancelEvent(userId, eventId, request);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmEventRequest(@PathVariable("userId") long userId,
                                   @PathVariable("eventId") long eventId,
                                   @PathVariable("reqId") long reqId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.confirmEventRequest(userId, eventId, reqId, request);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectEventRequest(@PathVariable("userId") long userId,
                                              @PathVariable("eventId") long eventId,
                                              @PathVariable("reqId") long reqId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.rejectEventRequest(userId, eventId, reqId, request);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") long userId,
                                                         HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto addNewRequest(@PathVariable("userId") long userId,
                                                 @RequestParam long eventId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return requestService.addNewRequest(userId, eventId, request);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") long userId,
                                              @PathVariable("requestId") long reqId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.cancelRequest(userId, reqId, request);
    }

    @PatchMapping("/subscribe")
    public void subscribeUser(@PathVariable("userId") long userId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        userService.subscribeUser(userId, request);
    }
}
