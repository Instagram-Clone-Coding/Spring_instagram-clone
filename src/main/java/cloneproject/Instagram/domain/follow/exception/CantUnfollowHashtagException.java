package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUnfollowHashtagException extends BusinessException{
    public CantUnfollowHashtagException(){
        super(ErrorCode.CANT_UNFOLLOW_HASHTAG);
    }
}
