package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class AccountDoesNotMatch extends BusinessException{
    public AccountDoesNotMatch(){
        super(ErrorCode.ACCOUNT_DOES_NOT_MATCH);
    }
}
