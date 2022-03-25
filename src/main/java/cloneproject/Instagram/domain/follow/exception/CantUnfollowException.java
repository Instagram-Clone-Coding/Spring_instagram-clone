package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUnfollowException extends BusinessException{
    public CantUnfollowException(){
        super(ErrorCode.CANT_UNFOLLOW);
    }
}
