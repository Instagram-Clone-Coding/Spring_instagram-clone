package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MessageLikeAlreadyExistException extends BusinessException {

    public MessageLikeAlreadyExistException() {
        super(ErrorCode.MESSAGE_LIKE_ALREADY_EXIST);
    }
}
