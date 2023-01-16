package explorewithme.model.request;

import explorewithme.model.other.DateUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DateUtils.formatter),
                request.getEvent(),
                request.getRequester(),
                request.getStatus());
    }
}
