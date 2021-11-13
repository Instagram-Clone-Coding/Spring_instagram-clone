package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MemberDoesNotExistException extends BusinessException{
    public MemberDoesNotExistException(){
        super(ErrorCode.MEMBER_DOES_NOT_EXIST);
    }
}
