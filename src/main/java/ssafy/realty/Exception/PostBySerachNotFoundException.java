package ssafy.realty.Exception;

import static ssafy.realty.Exception.PostExceptionList.POST_BY_SEARCH_NOT_FOUND;

public class PostBySerachNotFoundException extends PostException {
    public PostBySerachNotFoundException() {
        super(POST_BY_SEARCH_NOT_FOUND.getStatusCode(), POST_BY_SEARCH_NOT_FOUND.getHttpStatus(), POST_BY_SEARCH_NOT_FOUND.getMessage());
    }
}