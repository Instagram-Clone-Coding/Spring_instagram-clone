package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class UseridAlreadyExistException extends BusinessException{
        public UseridAlreadyExistException(){
            super(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
}
