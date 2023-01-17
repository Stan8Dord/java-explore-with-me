package explorewithme.exceptions;

import javax.servlet.http.HttpServletRequest;

public class NotFoundCategoryException extends NotFoundException {
    public NotFoundCategoryException(Long catId, HttpServletRequest request) {
        super(String.format("Категория %d не найдена. Request path = %s", catId, request.getRequestURI()));
    }
}
