package cloneproject.Instagram.domain.dm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class ChatRoomNotFoundException extends BusinessException {

	public ChatRoomNotFoundException() {
		super(ErrorCode.CHAT_ROOM_NOT_FOUND);
	}

}
