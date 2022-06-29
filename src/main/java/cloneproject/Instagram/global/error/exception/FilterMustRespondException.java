package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class FilterMustRespondException extends BusinessException {

	public FilterMustRespondException() {
		super(ErrorCode.FILTER_MUST_RESPOND);
	}

}
