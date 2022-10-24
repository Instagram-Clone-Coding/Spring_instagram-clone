package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class EmailSendFailException extends BusinessException {
	public EmailSendFailException() {
		super(ErrorCode.EMAIL_SEND_FAIL);
	}

}
