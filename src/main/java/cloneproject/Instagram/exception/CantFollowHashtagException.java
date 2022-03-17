package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantFollowHashtagException extends BusinessException{
    public CantFollowHashtagException(){
        super(ErrorCode.CANT_FOLLOW_HASHTAG);
    }
}