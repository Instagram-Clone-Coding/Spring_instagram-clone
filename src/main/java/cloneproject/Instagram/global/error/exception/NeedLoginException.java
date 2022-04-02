package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class NeedLoginException extends BusinessException{
    public NeedLoginException(){
        super(ErrorCode.NEED_TO_LOGIN);
    }
}
