package explorewithme.model.request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(formatter),
                request.getEvent(),
                request.getRequester(),
                request.getStatus());
    }
}
