package at.htlstp.rest_staff.presentation;

import at.htlstp.rest_staff.domain.exceptions.NoSuchEmployeeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmployeeAdvice {

    @ExceptionHandler(NoSuchEmployeeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoSuchElementException(NoSuchEmployeeException e) {
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidArguments(MethodArgumentNotValidException e) {
        return new ApiError(e.getMessage());
    }
}
