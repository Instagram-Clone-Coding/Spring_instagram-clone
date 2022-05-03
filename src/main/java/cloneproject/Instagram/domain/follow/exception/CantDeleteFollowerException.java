package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantDeleteFollowerException extends BusinessException{
    public CantDeleteFollowerException(){
        super(ErrorCode.CANT_DELETE_FOLLOWER);
    }
}