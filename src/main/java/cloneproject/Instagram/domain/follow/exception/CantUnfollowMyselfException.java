package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUnfollowMyselfException extends BusinessException{
    public CantUnfollowMyselfException(){
        super(ErrorCode.CANT_UNFOLLOW_MYSELF);
    }
}
