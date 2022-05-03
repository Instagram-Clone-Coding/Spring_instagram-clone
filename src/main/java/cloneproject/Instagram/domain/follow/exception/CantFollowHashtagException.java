package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantFollowHashtagException extends BusinessException{
    public CantFollowHashtagException(){
        super(ErrorCode.CANT_FOLLOW_HASHTAG);
    }
}