package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantDeleteCommentException extends BusinessException {

	public CantDeleteCommentException() {
		super(ErrorCode.COMMENT_CANT_DELETE);
	}

}
