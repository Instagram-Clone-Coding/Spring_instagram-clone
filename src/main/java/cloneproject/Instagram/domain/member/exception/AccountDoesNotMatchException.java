package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class AccountDoesNotMatchException extends BusinessException{
    public AccountDoesNotMatchException(){
        super(ErrorCode.ACCOUNT_NOT_MATCHED);
    }
}
