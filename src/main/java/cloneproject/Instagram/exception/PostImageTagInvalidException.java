package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;
import cloneproject.Instagram.dto.error.ErrorResponse;

import java.util.List;

public class PostImageTagInvalidException extends BusinessException {

    public PostImageTagInvalidException() {
        super(ErrorCode.INVALID_POST_IMAGE_TAG);
    }

    public PostImageTagInvalidException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode, errors);
    }
}
