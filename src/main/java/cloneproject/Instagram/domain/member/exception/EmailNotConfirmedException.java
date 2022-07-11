package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class EmailNotConfirmedException extends BusinessException {
	public EmailNotConfirmedException() {
		super(ErrorCode.EMAIL_NOT_CONFIRMED);
	}

}
