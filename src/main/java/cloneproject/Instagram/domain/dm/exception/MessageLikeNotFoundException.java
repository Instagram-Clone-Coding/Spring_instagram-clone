package cloneproject.Instagram.domain.dm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MessageLikeNotFoundException extends BusinessException {

    public MessageLikeNotFoundException() {
        super(ErrorCode.MESSAGE_LIKE_NOT_FOUND);
    }
}
