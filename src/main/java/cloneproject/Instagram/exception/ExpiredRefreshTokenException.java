package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class ExpiredRefreshTokenException extends BusinessException{
    public ExpiredRefreshTokenException(){
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
}
