package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UsernameAlreadyExistException extends BusinessException {
	public UsernameAlreadyExistException() {
		super(ErrorCode.USERNAME_ALREADY_EXIST);
	}
}
