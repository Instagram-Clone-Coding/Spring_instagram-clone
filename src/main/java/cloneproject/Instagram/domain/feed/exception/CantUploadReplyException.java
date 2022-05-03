package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class CantUploadReplyException extends BusinessException {

	public CantUploadReplyException() {
		super(ErrorCode.REPLY_CANT_UPLOAD);
	}
}
