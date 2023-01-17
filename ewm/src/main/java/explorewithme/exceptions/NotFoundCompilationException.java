package explorewithme.exceptions;

import javax.servlet.http.HttpServletRequest;

public class NotFoundCompilationException extends NotFoundException {
    public NotFoundCompilationException(Long compId, HttpServletRequest request) {
        super(String.format("Подборка %d не найдена. Request path = %s", compId, request.getRequestURI()));
    }
}
