package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UnfollowFailException extends BusinessException {

    public UnfollowFailException() {
        super(ErrorCode.UNFOLLOW_FAIL);
    }

}
