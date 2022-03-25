package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantFollowMyselfException extends BusinessException {
    public CantFollowMyselfException(){
        super(ErrorCode.CANT_FOLLOW_MYSELF);
    }
}