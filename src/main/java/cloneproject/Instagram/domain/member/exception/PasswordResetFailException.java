package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PasswordResetFailException extends BusinessException {
	public PasswordResetFailException() {
		super(ErrorCode.PASSWORD_RESET_FAIL);
	}
}
