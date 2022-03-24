package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostLikeNotFoundException extends BusinessException {

    public PostLikeNotFoundException() {
        super(ErrorCode.POST_LIKE_NOT_FOUND);
    }
}
