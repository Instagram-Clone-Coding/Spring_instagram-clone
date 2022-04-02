package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class NoConfirmEmailException extends BusinessException{
    public NoConfirmEmailException(){
        super(ErrorCode.EMAIL_NOT_CONFIRMED);
    }
}
