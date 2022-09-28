package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PasswordEqualWithOldException extends BusinessException {

	public PasswordEqualWithOldException() {
		super(ErrorCode.PASSWORD_EQUAL_WITH_OLD);
	}

}
