package at.htlstp.rest_staff.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchEmployeeException extends ResponseStatusException {
    public NoSuchEmployeeException() {
        super(HttpStatus.NOT_FOUND);
    }

}
