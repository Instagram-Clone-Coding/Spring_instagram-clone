package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class AlreadyFollowException extends BusinessException{
    public AlreadyFollowException(){
        super(ErrorCode.ALREADY_FOLLOW);
    }
}
