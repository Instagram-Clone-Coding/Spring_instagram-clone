package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantResetPasswordException extends BusinessException{
    public CantResetPasswordException(){
        super(ErrorCode.CANT_RESET_PASSWORD);
    }
}
