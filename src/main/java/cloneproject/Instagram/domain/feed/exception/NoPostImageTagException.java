package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class NoPostImageTagException extends BusinessException {

    public NoPostImageTagException() {
        super(ErrorCode.NO_POST_IMAGE_TAG);
    }
}
