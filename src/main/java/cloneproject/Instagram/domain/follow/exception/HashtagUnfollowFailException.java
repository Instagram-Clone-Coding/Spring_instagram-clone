package cloneproject.Instagram.domain.follow.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class HashtagUnfollowFailException extends BusinessException {

	public HashtagUnfollowFailException() {
		super(ErrorCode.HASHTAG_UNFOLLOW_FAIL);
	}

}
