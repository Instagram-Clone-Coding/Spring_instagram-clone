package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class PostImagesAndAltTextsMismatchException extends BusinessException {

    public PostImagesAndAltTextsMismatchException() {
        super(ErrorCode.POST_IMAGES_AND_ALT_TEXTS_MISMATCH);
    }
}
