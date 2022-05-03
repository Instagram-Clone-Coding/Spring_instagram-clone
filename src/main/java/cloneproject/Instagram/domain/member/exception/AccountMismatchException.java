package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class AccountMismatchException extends BusinessException{
    public AccountMismatchException(){
        super(ErrorCode.ACCOUNT_MISMATCH);
    }
}
