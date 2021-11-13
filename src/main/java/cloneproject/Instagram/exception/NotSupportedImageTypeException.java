package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class NotSupportedImageTypeException extends BusinessException {
    public NotSupportedImageTypeException() {
        super(ErrorCode.NOT_SUPPORTED_IMAGE_TYPE);
    }
}
