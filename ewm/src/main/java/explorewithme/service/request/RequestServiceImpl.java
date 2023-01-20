package explorewithme.service.request;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.NotFoundEventException;
import explorewithme.model.event.Event;
import explorewithme.model.event.EventState;
import explorewithme.model.request.dto.ParticipationRequestDto;
import explorewithme.model.request.Request;
import explorewithme.model.request.RequestMapper;
import explorewithme.repository.EventRepository;
import explorewithme.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByUser(long userId, long eventId) {
        List<Request> requests = requestRepository.findByEvent(eventId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        List<Request> requests = requestRepository.findByRequester(userId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addNewRequest(long userId, long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundEventException(eventId, request));
        List<Request> requests = requestRepository.findByRequester(userId);
        List<Long> reqIds = requests.stream().map(Request::getId).collect(Collectors.toList());

        if (!event.getState().equals(EventState.PUBLISHED) || event.getInitiator().getId() == userId
        || event.getConfirmedRequests() == event.getParticipantLimit() || reqIds.contains(userId))
            throw new BadRequestException(request.getParameterMap().toString());
        Request newRequest = new Request(null, LocalDateTime.now(), eventId, userId, "PENDING");
        if (!event.isRequestModeration())
            newRequest.setStatus("CONFIRMED");

        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }
}
