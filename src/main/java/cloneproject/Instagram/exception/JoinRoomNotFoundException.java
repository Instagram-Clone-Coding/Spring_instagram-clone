package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class JoinRoomNotFoundException extends BusinessException {

    public JoinRoomNotFoundException() {
        super(ErrorCode.JOIN_ROOM_NOT_FOUND);
    }
}
