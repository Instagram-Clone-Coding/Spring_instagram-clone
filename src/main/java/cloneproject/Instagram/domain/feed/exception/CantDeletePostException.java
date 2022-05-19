package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantDeletePostException extends BusinessException {

	public CantDeletePostException() {
		super(ErrorCode.POST_CANT_DELETE);
	}

}
