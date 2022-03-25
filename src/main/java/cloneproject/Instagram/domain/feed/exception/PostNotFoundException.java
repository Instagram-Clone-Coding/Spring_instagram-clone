package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
