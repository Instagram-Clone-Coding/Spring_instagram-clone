package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class NoPostImageTagException extends BusinessException {

    public NoPostImageTagException() {
        super(ErrorCode.NO_POST_IMAGE_TAG);
    }
}
