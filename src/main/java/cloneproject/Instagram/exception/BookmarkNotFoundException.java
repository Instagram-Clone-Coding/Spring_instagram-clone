package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class BookmarkNotFoundException extends BusinessException {

    public BookmarkNotFoundException() {
        super(ErrorCode.BOOKMARK_NOT_FOUND);
    }
}
