package ssafy.realty.Exception;

import org.springframework.http.HttpStatus;
import static ssafy.realty.Exception.PostExceptionList.DatabaseOperationException;

public class DatabaseOperationException extends PostException {
    public DatabaseOperationException() {
        super(DatabaseOperationException.getStatusCode(), DatabaseOperationException.getHttpStatus(), DatabaseOperationException.getMessage());
    }
}