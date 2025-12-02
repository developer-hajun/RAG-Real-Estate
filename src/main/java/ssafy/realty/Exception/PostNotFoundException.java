package ssafy.realty.Exception;

import static ssafy.realty.Exception.PostExceptionList.POST_NOT_FOUND;

public class PostNotFoundException extends PostException {
    public PostNotFoundException(String error ) {
        super(POST_NOT_FOUND.getStatusCode(), POST_NOT_FOUND.getHttpStatus(), POST_NOT_FOUND.getMessage());
    }
}