package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantCreateCommentException extends BusinessException {

    public CantCreateCommentException() {
        super(ErrorCode.COMMENT_CANT_CREATE);
    }
}
