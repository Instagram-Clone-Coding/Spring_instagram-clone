package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantUnfollowException extends BusinessException{
    public CantUnfollowException(){
        super(ErrorCode.CANT_UNFOLLOW);
    }
}
