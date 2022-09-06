package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class BlockMyselfFailException extends BusinessException {
	public BlockMyselfFailException() {
		super(ErrorCode.BLOCK_MYSELF_FAIL);
	}

}