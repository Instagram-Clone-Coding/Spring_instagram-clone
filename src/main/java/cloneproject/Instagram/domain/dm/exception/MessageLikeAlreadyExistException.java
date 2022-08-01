package cloneproject.Instagram.domain.dm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MessageLikeAlreadyExistException extends BusinessException {

	public MessageLikeAlreadyExistException() {
		super(ErrorCode.MESSAGE_LIKE_ALREADY_EXIST);
	}

}
