package cloneproject.Instagram.global.error.exception;

import cloneproject.Instagram.global.error.ErrorCode;

public class EntityTypeInvalidException extends BusinessException {

	public EntityTypeInvalidException() {
		super(ErrorCode.ENTITY_TYPE_INVALID);
	}

}
