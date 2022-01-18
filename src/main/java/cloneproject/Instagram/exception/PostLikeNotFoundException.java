package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class PostLikeNotFoundException extends BusinessException {

    public PostLikeNotFoundException() {
        super(ErrorCode.POST_LIKE_NOT_FOUND);
    }
}
