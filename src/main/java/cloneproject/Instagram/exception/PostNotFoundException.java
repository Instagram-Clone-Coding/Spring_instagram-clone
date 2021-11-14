package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
