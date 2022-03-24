package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantResetPasswordException extends BusinessException{
    public CantResetPasswordException(){
        super(ErrorCode.CANT_RESET_PASSWORD);
    }
}
