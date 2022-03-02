package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantCreateCommentException extends BusinessException {

    public CantCreateCommentException() {
        super(ErrorCode.COMMENT_CANT_CREATE);
    }
}
