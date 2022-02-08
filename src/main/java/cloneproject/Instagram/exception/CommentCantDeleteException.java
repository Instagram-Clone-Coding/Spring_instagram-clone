package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CommentCantDeleteException extends BusinessException {

    public CommentCantDeleteException() {
        super(ErrorCode.COMMENT_CANT_DELETE);
    }
}
