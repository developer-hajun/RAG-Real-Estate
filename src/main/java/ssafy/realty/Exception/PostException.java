package ssafy.realty.Exception;

import org.springframework.http.HttpStatus;

public abstract class PostException extends ApplicationException {
    protected PostException(String statusCode, HttpStatus httpStatus, String message) {
        super(statusCode, httpStatus, message);
    }
}