package cloneproject.Instagram.domain.alarm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class MismatchedAlarmTypeException extends BusinessException {

	public MismatchedAlarmTypeException() {
		super(ErrorCode.MISMATCHED_ALARM_TYPE);
	}

}
