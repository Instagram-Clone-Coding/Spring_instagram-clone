package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class AlreadyBlockException extends BusinessException{
    public AlreadyBlockException(){
        super(ErrorCode.ALREADY_BLOCK);
    }
    
}
