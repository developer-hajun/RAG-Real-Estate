package ssafy.realty.Exception.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException{
    private final String statusCode;
    private final HttpStatus httpStatus;

    public ApplicationException(String statusCode, HttpStatus httpStatus, String message) {
        super(message);
        this.statusCode = statusCode;
        this.httpStatus = httpStatus;
    }

}