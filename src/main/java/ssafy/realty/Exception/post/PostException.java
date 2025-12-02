package ssafy.realty.Exception.post;

import org.springframework.http.HttpStatus;
import ssafy.realty.Exception.global.ApplicationException;

public abstract class PostException extends ApplicationException {
    protected PostException(String statusCode, HttpStatus httpStatus, String message) {
        super(statusCode, httpStatus, message);
    }
}