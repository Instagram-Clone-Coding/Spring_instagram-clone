package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MemberStoryNotFoundException extends BusinessException {

    public MemberStoryNotFoundException() {
        super(ErrorCode.MEMBER_STORY_NOT_FOUND);
    }
}
