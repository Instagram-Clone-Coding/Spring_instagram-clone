package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class HashtagFollowFailException extends BusinessException {

	public HashtagFollowFailException() {
		super(ErrorCode.HASHTAG_FOLLOW_FAIL);
	}

}
