package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class CantConvertFileException extends BusinessException{
    public CantConvertFileException(){
        super(ErrorCode.FILE_CANT_CONVERT);
    }
    
}
