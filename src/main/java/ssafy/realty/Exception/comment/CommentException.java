package ssafy.realty.Exception.comment;

import org.springframework.http.HttpStatus;
import ssafy.realty.Exception.global.ApplicationException;

public abstract class CommentException extends ApplicationException {
    protected CommentException(String statusCode, HttpStatus httpStatus, String message) {
        super(statusCode, httpStatus, message);
    }
}