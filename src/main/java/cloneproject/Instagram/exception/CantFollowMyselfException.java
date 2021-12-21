package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantFollowMyselfException extends BusinessException {
    public CantFollowMyselfException(){
        super(ErrorCode.CANT_FOLLOW_MYSELF);
    }
}