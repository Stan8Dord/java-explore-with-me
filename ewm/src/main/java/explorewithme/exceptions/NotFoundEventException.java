package explorewithme.exceptions;

import javax.servlet.http.HttpServletRequest;

public class NotFoundEventException extends NotFoundException {
    public NotFoundEventException(Long eventId, HttpServletRequest request) {
        super(String.format("Событие %d не найдено. Request path = %s", eventId, request.getRequestURI()));
    }
}
