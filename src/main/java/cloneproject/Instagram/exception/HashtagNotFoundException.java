package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class HashtagNotFoundException extends BusinessException{
    public HashtagNotFoundException(){
        super(ErrorCode.HASHTAG_NOT_FOUND);
    }
}