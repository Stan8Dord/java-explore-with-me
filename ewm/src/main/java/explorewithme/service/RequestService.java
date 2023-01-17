package explorewithme.service;

import explorewithme.model.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getEventRequestsByUser(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto addNewRequest(long userId, long eventId, HttpServletRequest request);
}
