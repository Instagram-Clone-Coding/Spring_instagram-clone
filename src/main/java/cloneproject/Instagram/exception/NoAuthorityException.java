package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class NoAuthorityException extends BusinessException{
    public NoAuthorityException(){
        super(ErrorCode.NO_AUTHORITY);
    }
}
