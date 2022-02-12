package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class NoConfirmEmailException extends BusinessException{
    public NoConfirmEmailException(){
        super(ErrorCode.NO_CONFIRM_EMAIL);
    }
}
