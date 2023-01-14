package explorewithme.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        return new ApiError(null, e.getMessage(),
                "For the requested operation the conditions are not met.", "FORBIDDEN",
                LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        return new ApiError(null, e.getMessage(),
                "The required object was not found.", "FORBIDDEN",
                LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated",
                "CONFLICT", LocalDateTime.now().toString());
    }
}
