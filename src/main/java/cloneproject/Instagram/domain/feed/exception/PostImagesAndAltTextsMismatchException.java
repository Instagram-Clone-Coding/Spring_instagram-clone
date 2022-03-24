package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostImagesAndAltTextsMismatchException extends BusinessException {

    public PostImagesAndAltTextsMismatchException() {
        super(ErrorCode.POST_IMAGES_AND_ALT_TEXTS_MISMATCH);
    }
}
