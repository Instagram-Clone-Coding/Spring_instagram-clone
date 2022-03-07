package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantSendEmailException extends BusinessException{
    public CantSendEmailException(){
        super(ErrorCode.CANT_SEND_EMAIL);
    }
    
}
