package ssafy.realty.Exception.global;


import static ssafy.realty.Exception.post.PostExceptionList.DatabaseOperationException;

public class DatabaseOperationException extends ApplicationException {
    public DatabaseOperationException(String message) {
        super(DatabaseOperationException.getStatusCode(), DatabaseOperationException.getHttpStatus(), message);
    }
}