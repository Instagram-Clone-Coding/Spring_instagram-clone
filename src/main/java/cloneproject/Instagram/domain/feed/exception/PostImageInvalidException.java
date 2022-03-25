package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostImageInvalidException extends BusinessException {

    public PostImageInvalidException() {
        super(ErrorCode.INVALID_POST_IMAGE);
    }
}
