package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class NotSupportedImageTypeException extends BusinessException {
	public NotSupportedImageTypeException() {
		super(ErrorCode.IMAGE_TYPE_NOT_SUPPORTED);
	}
}
