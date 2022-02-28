package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MessageLikeNotFoundException extends BusinessException {

    public MessageLikeNotFoundException() {
        super(ErrorCode.MESSAGE_LIKE_NOT_FOUND);
    }
}
