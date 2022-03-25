package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UseridAlreadyExistException extends BusinessException{
        public UseridAlreadyExistException(){
            super(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
}
