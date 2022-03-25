package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CommentLikeAlreadyExistException extends BusinessException {

    public CommentLikeAlreadyExistException() {
        super(ErrorCode.COMMENT_LIKE_ALREADY_EXIST);
    }
}
