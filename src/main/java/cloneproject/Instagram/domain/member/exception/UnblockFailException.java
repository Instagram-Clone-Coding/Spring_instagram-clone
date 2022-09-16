package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UnblockFailException extends BusinessException {
	public UnblockFailException() {
		super(ErrorCode.UNBLOCK_FAIL);
	}

}