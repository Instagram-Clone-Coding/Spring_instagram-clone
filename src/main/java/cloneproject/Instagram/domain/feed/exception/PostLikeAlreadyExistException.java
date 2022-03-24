package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostLikeAlreadyExistException extends BusinessException {

    public PostLikeAlreadyExistException() {
        super(ErrorCode.POST_LIKE_ALREADY_EXIST);
    }
}
