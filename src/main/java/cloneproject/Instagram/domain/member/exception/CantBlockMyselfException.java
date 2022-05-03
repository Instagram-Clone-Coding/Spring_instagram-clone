package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantBlockMyselfException extends BusinessException {
	public CantBlockMyselfException() {
		super(ErrorCode.CANT_BLOCK_MYSELF);
	}
}