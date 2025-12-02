package ssafy.realty.Exception.global;

import ssafy.realty.Exception.post.PostException;

import static ssafy.realty.Exception.post.PostExceptionList.DatabaseOperationException;

public class DatabaseOperationException extends ApplicationException {
    public DatabaseOperationException() {
        super(DatabaseOperationException.getStatusCode(), DatabaseOperationException.getHttpStatus(), DatabaseOperationException.getMessage());
    }
}