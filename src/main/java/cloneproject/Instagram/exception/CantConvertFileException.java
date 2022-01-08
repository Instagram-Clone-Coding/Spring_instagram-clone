package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantConvertFileException extends BusinessException{
    public CantConvertFileException(){
        super(ErrorCode.CANT_CONVERT_FILE);
    }
    
}
