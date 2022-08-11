package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class FileConvertFailException extends BusinessException {

	public FileConvertFailException() {
		super(ErrorCode.FILE_CONVERT_FAIL);
	}

}
