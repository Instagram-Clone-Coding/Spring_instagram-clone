package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class ExpiredRefreshTokenException extends BusinessException{
    public ExpiredRefreshTokenException(){
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
}
