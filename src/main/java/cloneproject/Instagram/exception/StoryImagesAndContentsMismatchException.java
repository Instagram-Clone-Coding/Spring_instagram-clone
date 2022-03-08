package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class StoryImagesAndContentsMismatchException extends BusinessException {

    public StoryImagesAndContentsMismatchException() {
        super(ErrorCode.STORY_IMAGES_AND_CONTENTS_MISMATCH);
    }
}
