package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class InvalidTagLocationException extends BusinessException {

    public InvalidTagLocationException(){
        super(ErrorCode.INVALID_TAG_LOCATION);
    }
}
