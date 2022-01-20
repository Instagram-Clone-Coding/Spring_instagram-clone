package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class PostLikeAlreadyExistException extends BusinessException {

    public PostLikeAlreadyExistException() {
        super(ErrorCode.POST_LIKE_ALREADY_EXIST);
    }
}
