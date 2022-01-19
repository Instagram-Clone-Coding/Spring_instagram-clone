package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class BookmarkAlreadyExistException extends BusinessException {

    public BookmarkAlreadyExistException() {
        super(ErrorCode.BOOKMARK_ALREADY_EXIST);
    }
}
