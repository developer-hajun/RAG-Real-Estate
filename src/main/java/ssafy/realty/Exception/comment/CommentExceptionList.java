package ssafy.realty.Exception.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentExceptionList {
    Comment_Not_Found_Exception("C001", HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다.");

    private final String statusCode;
    private final HttpStatus httpStatus;
    private final String message;

}