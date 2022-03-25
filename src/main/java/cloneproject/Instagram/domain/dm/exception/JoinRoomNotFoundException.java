package cloneproject.Instagram.domain.dm.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class JoinRoomNotFoundException extends BusinessException {

    public JoinRoomNotFoundException() {
        super(ErrorCode.JOIN_ROOM_NOT_FOUND);
    }
}
