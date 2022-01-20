package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantUnblockMyselfException extends BusinessException {
    public CantUnblockMyselfException(){
        super(ErrorCode.CANT_UNBLOCK_MYSELF);
    }
}