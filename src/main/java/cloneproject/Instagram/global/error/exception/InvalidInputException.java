package cloneproject.Instagram.global.error.exception;

import java.util.List;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.ErrorResponse;

public class InvalidInputException extends BusinessException {

    public InvalidInputException(List<ErrorResponse.FieldError> errors) {
        super(ErrorCode.INPUT_VALUE_INVALID, errors);
    }

}
