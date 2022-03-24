package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostImageNotFoundException extends BusinessException {
    public PostImageNotFoundException() {
        super(ErrorCode.POST_IMAGE_NOT_FOUND);
    }
}
