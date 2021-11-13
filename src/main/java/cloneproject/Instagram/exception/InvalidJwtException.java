package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class InvalidJwtException extends BusinessException{
    public InvalidJwtException(){
        super(ErrorCode.INVALID_JWT);
    }
}
