package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class ExpiredAccessTokenException extends BusinessException {
	public ExpiredAccessTokenException() {
		super(ErrorCode.EXPIRED_ACCESS_TOKEN);
	}
}
