package cloneproject.Instagram.domain.hashtag.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class HashtagNotFoundException extends BusinessException{
    public HashtagNotFoundException(){
        super(ErrorCode.HASHTAG_NOT_FOUND);
    }
}