package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class FollowerDeleteFailException extends BusinessException {

    public FollowerDeleteFailException() {
        super(ErrorCode.FOLLOWER_DELETE_FAIL);
    }

}
