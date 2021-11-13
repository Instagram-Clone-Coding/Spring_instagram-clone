package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class NeedLoginException extends BusinessException{
    public NeedLoginException(){
        super(ErrorCode.NEED_LOGIN);
    }
}
