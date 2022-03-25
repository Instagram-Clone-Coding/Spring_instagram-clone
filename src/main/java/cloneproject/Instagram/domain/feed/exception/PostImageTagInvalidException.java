package cloneproject.Instagram.domain.feed.exception;

import java.util.List;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.ErrorResponse;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class PostImageTagInvalidException extends BusinessException {

    public PostImageTagInvalidException() {
        super(ErrorCode.INVALID_POST_IMAGE_TAG);
    }

    public PostImageTagInvalidException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode, errors);
    }
}
