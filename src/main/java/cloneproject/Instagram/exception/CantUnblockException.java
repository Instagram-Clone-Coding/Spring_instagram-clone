package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantUnblockException extends BusinessException {
    public CantUnblockException(){
        super(ErrorCode.CANT_UNBLOCK);
    }
}