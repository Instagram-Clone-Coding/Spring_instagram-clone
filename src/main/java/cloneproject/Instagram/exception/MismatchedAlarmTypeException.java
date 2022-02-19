package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class MismatchedAlarmTypeException extends BusinessException {

    public MismatchedAlarmTypeException() {
        super(ErrorCode.MISMATCHED_ALARM_TYPE);
    }
}
