package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class NotSupportedImageTypeException extends BusinessException {
    public NotSupportedImageTypeException() {
        super(ErrorCode.NOT_SUPPORTED_IMAGE_TYPE);
    }
}
