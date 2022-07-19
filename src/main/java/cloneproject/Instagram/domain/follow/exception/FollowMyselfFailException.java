package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class FollowMyselfFailException extends BusinessException {

    public FollowMyselfFailException() {
        super(ErrorCode.FOLLOW_MYSELF_FAIL);
    }

}
