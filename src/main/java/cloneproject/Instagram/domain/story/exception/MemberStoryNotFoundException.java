package cloneproject.Instagram.domain.story.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MemberStoryNotFoundException extends BusinessException {

	public MemberStoryNotFoundException() {
		super(ErrorCode.MEMBER_STORY_NOT_FOUND);
	}

}
