package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class ExpiredAccessTokenException extends BusinessException{
    public ExpiredAccessTokenException(){
        super(ErrorCode.EXPIRED_ACCESS_TOKEN);
    }
}
