package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class AlreadyFollowException extends BusinessException{
    public AlreadyFollowException(){
        super(ErrorCode.ALREADY_FOLLOW);
    }
}
