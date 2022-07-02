package cloneproject.Instagram.domain.hashtag.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class HashtagPrefixMismatchException extends BusinessException {

	public HashtagPrefixMismatchException() {
		super(ErrorCode.HASHTAG_PREFIX_MISMATCH);
	}

}
