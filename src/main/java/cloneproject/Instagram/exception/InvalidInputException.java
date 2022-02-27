package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;
import cloneproject.Instagram.dto.error.ErrorResponse;

import java.util.List;

public class InvalidInputException extends BusinessException {

    public InvalidInputException(List<ErrorResponse.FieldError> errors) {
        super(ErrorCode.INVALID_INPUT_VALUE, errors);
    }
}
