package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class AccountDoesNotMatchException extends BusinessException{
    public AccountDoesNotMatchException(){
        super(ErrorCode.ACCOUNT_DOES_NOT_MATCH);
    }
}
