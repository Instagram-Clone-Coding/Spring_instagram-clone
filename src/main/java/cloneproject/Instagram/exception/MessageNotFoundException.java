package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MessageNotFoundException extends BusinessException {

    public MessageNotFoundException() {
        super(ErrorCode.MESSAGE_NOT_FOUND);
    }
}
