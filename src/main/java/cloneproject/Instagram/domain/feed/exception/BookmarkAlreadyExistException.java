package cloneproject.Instagram.domain.feed.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class BookmarkAlreadyExistException extends BusinessException {

    public BookmarkAlreadyExistException() {
        super(ErrorCode.BOOKMARK_ALREADY_EXIST);
    }
}
