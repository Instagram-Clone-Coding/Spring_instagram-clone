package cloneproject.Instagram.domain.dm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MessageNotFoundException extends BusinessException {

	public MessageNotFoundException() {
		super(ErrorCode.MESSAGE_NOT_FOUND);
	}

}
