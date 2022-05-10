package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUnblockException extends BusinessException {
	public CantUnblockException() {
		super(ErrorCode.CANT_UNBLOCK);
	}

}