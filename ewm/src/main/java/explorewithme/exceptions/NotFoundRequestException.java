package explorewithme.exceptions;

import javax.servlet.http.HttpServletRequest;

public class NotFoundRequestException extends NotFoundException {
    public NotFoundRequestException(Long reqId, HttpServletRequest request) {
        super(String.format("Запрос %d не найден. Request path = %s", reqId, request.getRequestURI()));
    }
}
