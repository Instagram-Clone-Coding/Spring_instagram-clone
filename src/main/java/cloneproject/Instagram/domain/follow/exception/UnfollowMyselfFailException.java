package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UnfollowMyselfFailException extends BusinessException {

    public UnfollowMyselfFailException() {
        super(ErrorCode.UNFOLLOW_MYSELF_FAIL);
    }

}
