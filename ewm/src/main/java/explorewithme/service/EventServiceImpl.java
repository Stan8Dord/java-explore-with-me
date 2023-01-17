package explorewithme.service;

import explorewithme.client.StatClient;
import explorewithme.exceptions.*;
import explorewithme.model.category.Category;
import explorewithme.model.event.*;
import explorewithme.model.other.DateUtils;
import explorewithme.model.other.EndpointHit;
import explorewithme.model.other.Location;
import explorewithme.model.request.ParticipationRequestDto;
import explorewithme.model.request.Request;
import explorewithme.model.request.RequestMapper;
import explorewithme.model.user.User;
import explorewithme.repository.CategoryRepository;
import explorewithme.repository.EventRepository;
import explorewithme.repository.RequestRepository;
import explorewithme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository,
                            UserRepository userRepository, RequestRepository requestRepository, StatClient statClient) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.statClient = statClient;
    }

    @Override
    public List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort,
                                         int fromNum, int size, String ip, String uri, HttpServletRequest request) {
        Pageable page;
        int from = fromNum >= 0 ? fromNum / size : 0;
        Page<Event> events;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusYears(100);

        statClient.sendHit(new EndpointHit(null, "explorewithme", uri, ip,
                LocalDateTime.now()));

        if (sort == null || sort.equals("EVENT_DATE"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        else if (sort.equals("VIEWS"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
        else
            throw new BadRequestException(request.getParameterMap().toString());

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, DateUtils.formatter);
            end = LocalDateTime.parse(rangeEnd, DateUtils.formatter);
        }
        events = eventRepository.getEventsCustom(text, categories, paid, start, end, onlyAvailable, page);

        if (events == null)
            events = Page.empty();

        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(long eventId, String ip, String uri, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);

        statClient.sendHit(new EndpointHit(null, "explorewithme", uri, ip,
                LocalDateTime.now()));

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public Event checkEvent(long eventId, HttpServletRequest request) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundEventException(eventId, request));
    }

    @Override
    public List<EventShortDto> getEventsByUser(long userId, int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Event> shortEvents = eventRepository.findByUser(userId, page);

        return shortEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId, HttpServletRequest request) {
        return EventMapper.toEventFullDto(checkEventByUser(userId, eventId, request));
    }

    private Event checkEventByUser(long userId, long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        if (event.getInitiator().getId() != userId)
            throw new BadRequestException(request.getParameterMap().toString());
        else
            return event;
    }

    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto eventDto, HttpServletRequest request) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), DateUtils.formatter);

        if (eventDto.getAnnotation() == null || eventDate.isBefore(DateUtils.getTimeToMinutes().plusHours(2)))
            throw new BadRequestException(request.getParameterMap().toString());
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundCategoryException(eventDto.getCategory(), request));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(userId, request));

        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(eventDto, user, category)));
    }

    @Override
    public EventFullDto modifyEvent(long userId, UpdateEventRequest eventDto, HttpServletRequest req) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), DateUtils.formatter);
        long eventId = eventDto.getEventId();
        Event event = checkEventByUser(userId, eventId, req);

        if (event.getState() == EventState.PUBLISHED || eventDate.isBefore(DateUtils.getTimeToMinutes().plusHours(2)))
            throw new BadRequestException(req.getParameterMap().toString());
        if (event.getState().toString().equals("CANCELED"))
            event.setState(EventState.PENDING);
        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle(), req);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(long userId, long eventId, HttpServletRequest request) {
        Event event = checkEventByUser(userId, eventId, request);
        event.setState(EventState.CANCELED);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(Long[] users, String[] states, Long[] categories,
                                        String rangeStart, String rangeEnd, int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime start;
        LocalDateTime end;

        List<EventState> eStates = new ArrayList<>();
        if (states == null) {
            eStates = Arrays.asList(EventState.values());
        } else {
            for (String state : states) {
                eStates.add(EventState.valueOf(state));
            }
        }
        if (rangeStart == null)
            start = LocalDateTime.now().minusYears(100);
        else
            start = LocalDateTime.parse(rangeStart, DateUtils.formatter);
        if (rangeEnd == null)
            end = LocalDateTime.now().plusYears(100);
        else
            end = LocalDateTime.parse(rangeEnd, DateUtils.formatter);

        Page<Event> events = eventRepository.getEventsByAdmin(users, eStates, categories, start, end, page);

        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto modifyEventByAdmin(long eventId, AdminUpdateEventRequest eventDto, HttpServletRequest req) {
        Event event = checkEvent(eventId, req);
        Location location = eventDto.getLocation();

        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle(), req);
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        if (eventDto.getRequestModeration() != null)
            event.setRequestModeration(eventDto.getRequestModeration());

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event checkUpdates(Event event, String annotation, String description, Long catId, String eventDate,
                                Boolean paid, Integer participantLimit, String title, HttpServletRequest request) {
        if (annotation != null)
            event.setAnnotation(annotation);
        if (description != null)
            event.setDescription(description);
        if (catId != null) {
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundCategoryException(catId, request));
            event.setCategory(category);
        }
        if (eventDate != null)
            event.setEventDate(LocalDateTime.parse(eventDate, DateUtils.formatter));
        if (paid != null)
            event.setPaid(paid);
        if (participantLimit != null)
            event.setParticipantLimit(participantLimit);
        if (title != null)
            event.setTitle(title);

        return event;
    }

    @Override
    public EventFullDto publishEvent(long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        if (event.getEventDate()
                .isBefore(DateUtils.getTimeToMinutes().plusHours(1)) || !event.getState().equals(EventState.PENDING))
            throw new BadRequestException(request.getParameterMap().toString());

        event.setState(EventState.PUBLISHED);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        if (event.getState().equals(EventState.PUBLISHED))
            throw new BadRequestException(request.getParameterMap().toString());
        event.setState(EventState.CANCELED);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public ParticipationRequestDto confirmEventRequest(long userId, long eventId, long reqId,
                                                       HttpServletRequest httpReq) {
        Event event = checkEventByUser(userId, eventId, httpReq);
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));
        long limit = event.getParticipantLimit();
        long confirmedRequests = event.getConfirmedRequests();
        if (limit == 0 || !event.isRequestModeration() || limit == confirmedRequests)
            throw new BadRequestException(httpReq.getParameterMap().toString());

        if (confirmedRequests < event.getParticipantLimit()) {
            event.setConfirmedRequests(confirmedRequests + 1);
            request.setStatus("CONFIRMED");
            eventRepository.save(event);
            requestRepository.save(request);
        } else
            throw new BadRequestException(httpReq.getParameterMap().toString());

        if (limit == event.getConfirmedRequests()) {
            List<Request> reqs = requestRepository.findByEvent(eventId);
            reqs.stream().filter(req -> req.getStatus().equals("PENDING")).forEach((req) -> {
                req.setStatus("REJECTED");
                requestRepository.save(req);
            });
        }

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto rejectEventRequest(long userId, long eventId, long reqId,
                                                      HttpServletRequest httpReq) {
        Event event = checkEventByUser(userId, eventId, httpReq);
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));

        long num = event.getConfirmedRequests() - 1;
        event.setConfirmedRequests(num >= 0 ? num : 0);
        request.setStatus("REJECTED");
        eventRepository.save(event);
        requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long reqId, HttpServletRequest httpReq) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));
        Event event = checkEvent(request.getEvent(), httpReq);
        if (request.getRequester() == userId) {
            request.setStatus("CANCELED");
            long num = event.getConfirmedRequests() - 1;
            event.setConfirmedRequests(num >= 0 ? num : 0);
        } else
            throw new BadRequestException(httpReq.getParameterMap().toString());

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
