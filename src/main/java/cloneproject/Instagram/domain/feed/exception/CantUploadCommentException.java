package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUploadCommentException extends BusinessException {

	public CantUploadCommentException() {
		super(ErrorCode.COMMENT_CANT_UPLOAD);
	}
}
