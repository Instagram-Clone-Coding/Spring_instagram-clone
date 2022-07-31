package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class CantSendEmailException extends BusinessException {
	public CantSendEmailException() {
		super(ErrorCode.CANT_SEND_EMAIL);
	}

}
