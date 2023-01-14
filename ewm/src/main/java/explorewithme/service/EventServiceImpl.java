package explorewithme.service;

import explorewithme.client.StatClient;
import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.NotFoundException;
import explorewithme.model.category.Category;
import explorewithme.model.event.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime  now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @Override
    public List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort,
                                         int fromNum, int size, String ip, String uri) {
        Pageable page;
        int from = fromNum >= 0 ? fromNum / size : 0;
        LocalDateTime now = LocalDateTime.now();
        Page<Event> events;

        statClient.sendHit(new EndpointHit(null, "explorewithme", uri, ip,
                LocalDateTime.now().format(formatter)));

        if (sort == null || sort.equals("EVENT_DATE"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        else if (sort.equals("VIEWS"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
        else
            throw new BadRequestException("Некорректный параметр sort = " + sort);

        if (rangeStart == null || rangeEnd == null)
            events = eventRepository.getEventsCustom(text, categories, paid, now, onlyAvailable, page);
        else
            events = eventRepository.getEventsCustom(text, categories, paid, LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), onlyAvailable, page);
        if (events == null)
            events = Page.empty();

        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(long eventId, String ip, String uri) {
        Event event = checkEvent(eventId);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);

        statClient.sendHit(new EndpointHit(null, "explorewithme", uri, ip,
                LocalDateTime.now().format(formatter)));

        return EventMapper.toEventFullDto(event);
    }

    public Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found"));
    }

    @Override
    public List<EventShortDto> getEventsByUser(long userId, int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Event> shortEvents = eventRepository.findByUser(userId, page);

        return shortEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        return EventMapper.toEventFullDto(checkEventByUser(userId, eventId));
    }

    private Event checkEventByUser(long userId, long eventId) {
        Event event = checkEvent(eventId);
        if (event.getInitiator().getId() != userId)
            throw new BadRequestException("Событие добавлено другим пользователем!");
        else
            return event;
    }

    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto eventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);

        if (eventDto.getAnnotation() == null || eventDate.isBefore(now.plusHours(2)))
            throw new BadRequestException("Bad request");
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(eventDto, user, category)));
    }

    @Override
    public EventFullDto modifyEvent(long userId, UpdateEventRequest eventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
        long eventId = eventDto.getEventId();
        Event event = checkEventByUser(userId, eventId);

        if (event.getState() == EventState.PUBLISHED || eventDate.isBefore(now.plusHours(2)))
            throw new BadRequestException("Некорректный запрос!");
        if (event.getState().toString().equals("CANCELED"))
            event.setState(EventState.PENDING);
        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle());

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(long userId, long eventId) {
        Event event = checkEventByUser(userId, eventId);
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
            start = LocalDateTime.parse(rangeStart, formatter);
        if (rangeEnd == null)
            end = LocalDateTime.now().plusYears(100);
        else
            end = LocalDateTime.parse(rangeEnd, formatter);

        Page<Event> events = eventRepository.getEventsByAdmin(users, eStates, categories, start, end, page);

        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto modifyEventByAdmin(long eventId, AdminUpdateEventRequest eventDto) {
        Event event = checkEvent(eventId);
        Location location = eventDto.getLocation();

        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle());
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        if (eventDto.getRequestModeration() != null)
            event.setRequestModeration(eventDto.getRequestModeration());

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event checkUpdates(Event event, String annotation, String description, Long category2,
                               String eventDate, Boolean paid, Integer participantLimit, String title) {
        if (annotation != null)
            event.setAnnotation(annotation);
        if (description != null)
            event.setDescription(description);
        if (category2 != null) {
            Category category = categoryRepository.findById(category2)
                    .orElseThrow(() -> new NotFoundException("Категория не найдена!"));
            event.setCategory(category);
        }
        if (eventDate != null)
            event.setEventDate(LocalDateTime.parse(eventDate, formatter));
        if (paid != null)
            event.setPaid(paid);
        if (participantLimit != null)
            event.setParticipantLimit(participantLimit);
        if (title != null)
            event.setTitle(title);

        return event;
    }

    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = checkEvent(eventId);
        if (event.getEventDate().isBefore(now.plusHours(1)) || !event.getState().equals(EventState.PENDING))
            throw new BadRequestException("Bad request");

        event.setState(EventState.PUBLISHED);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = checkEvent(eventId);
        if (event.getState().equals(EventState.PUBLISHED))
            throw new BadRequestException("Bad request");
        event.setState(EventState.CANCELED);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmEventRequest(long userId, long eventId, long reqId) {
        Event event = checkEventByUser(userId, eventId);
        Request request = requestRepository.findById(reqId).orElseThrow(() -> new NotFoundException("Not found!"));
        long limit = event.getParticipantLimit();
        long confirmedRequests = event.getConfirmedRequests();
        if (limit == 0 || !event.isRequestModeration() || limit == confirmedRequests)
            throw new BadRequestException("Bad request");

        if (confirmedRequests < event.getParticipantLimit()) {
            event.setConfirmedRequests(confirmedRequests + 1);
            request.setStatus("CONFIRMED");
            eventRepository.save(event);
            requestRepository.save(request);
        } else
            throw new BadRequestException("Bad request");

        if (limit == event.getConfirmedRequests()) {
            List<Request> reqs = requestRepository.findByEvent(eventId);
            reqs.stream().filter(req -> req.getStatus().equals("PENDING")).forEach((req) -> {
                req.setStatus("REJECTED");
                requestRepository.save(req);});
        }

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectEventRequest(long userId, long eventId, long reqId) {
        Event event = checkEventByUser(userId, eventId);
        Request request = requestRepository.findById(reqId).orElseThrow(() -> new NotFoundException("Not found!"));

        long num = event.getConfirmedRequests() - 1;
        event.setConfirmedRequests(num >= 0 ? num : 0);
        request.setStatus("REJECTED");
        eventRepository.save(event);
        requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long reqId) {
        Request request = requestRepository.findById(reqId).orElseThrow(() -> new NotFoundException("Not found"));
        Event event = checkEvent(request.getEvent());
        if (request.getRequester() == userId) {
            request.setStatus("CANCELED");
            long num = event.getConfirmedRequests() - 1;
            event.setConfirmedRequests(num >= 0 ? num : 0);
        } else
            throw new BadRequestException("Чужой запрос");

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
