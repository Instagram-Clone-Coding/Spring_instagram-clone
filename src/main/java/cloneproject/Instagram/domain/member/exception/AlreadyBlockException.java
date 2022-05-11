package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class AlreadyBlockException extends BusinessException {
	public AlreadyBlockException() {
		super(ErrorCode.ALREADY_BLOCK);
	}

}
