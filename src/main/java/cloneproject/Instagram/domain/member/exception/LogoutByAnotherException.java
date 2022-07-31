package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class LogoutByAnotherException extends BusinessException {

	public LogoutByAnotherException() {
		super(ErrorCode.LOGOUT_BY_ANOTHER);
	}
}