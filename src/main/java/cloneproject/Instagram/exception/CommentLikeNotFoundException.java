package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CommentLikeNotFoundException extends BusinessException{

    public CommentLikeNotFoundException() {
        super(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    }
}
