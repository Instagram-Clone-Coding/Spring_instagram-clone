package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class PostImageInvalidException extends BusinessException {

    public PostImageInvalidException() {
        super(ErrorCode.INVALID_POST_IMAGE);
    }
}
