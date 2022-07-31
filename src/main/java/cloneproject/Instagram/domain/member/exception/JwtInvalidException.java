package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class JwtInvalidException extends BusinessException {
	public JwtInvalidException() {
		super(ErrorCode.JWT_INVALID);
	}

}
