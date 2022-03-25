package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUnblockMyselfException extends BusinessException {
    public CantUnblockMyselfException(){
        super(ErrorCode.CANT_UNBLOCK_MYSELF);
    }
}