package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CommentLikeNotFoundException extends BusinessException{

    public CommentLikeNotFoundException() {
        super(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    }
}
