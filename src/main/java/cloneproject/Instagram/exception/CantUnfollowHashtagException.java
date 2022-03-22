package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantUnfollowHashtagException extends BusinessException{
    public CantUnfollowHashtagException(){
        super(ErrorCode.CANT_UNFOLLOW_HASHTAG);
    }
}
