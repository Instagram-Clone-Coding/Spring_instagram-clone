package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class InvalidJwtException extends BusinessException {
	public InvalidJwtException() {
		super(ErrorCode.INVALID_JWT);
	}
}
