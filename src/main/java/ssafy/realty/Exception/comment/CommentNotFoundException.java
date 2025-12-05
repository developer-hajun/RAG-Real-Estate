package ssafy.realty.Exception.comment;


import static ssafy.realty.Exception.comment.CommentExceptionList.Comment_Not_Found_Exception;

public class CommentNotFoundException extends CommentException {
    public CommentNotFoundException(String s) {
        super(Comment_Not_Found_Exception.getStatusCode(), Comment_Not_Found_Exception.getHttpStatus(), s);
    }
}