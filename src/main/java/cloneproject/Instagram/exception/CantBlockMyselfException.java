package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class CantBlockMyselfException extends BusinessException {
    public CantBlockMyselfException(){
        super(ErrorCode.CANT_BLOCK_MYSELF);
    }
}