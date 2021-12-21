package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantUnfollowMyselfException extends BusinessException{
    public CantUnfollowMyselfException(){
        super(ErrorCode.CANT_UNFOLLOW_MYSELF);
    }
}
