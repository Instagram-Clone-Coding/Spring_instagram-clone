package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MemberDoesNotExistException extends BusinessException {
	public MemberDoesNotExistException() {
		super(ErrorCode.MEMBER_NOT_FOUND);
	}

}
