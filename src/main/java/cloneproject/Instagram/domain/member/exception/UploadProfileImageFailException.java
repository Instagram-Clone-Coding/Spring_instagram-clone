package cloneproject.Instagram.domain.member.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class UploadProfileImageFailException extends BusinessException{
    public UploadProfileImageFailException() {
        super(ErrorCode.UPLOAD_PROFILE_IMAGE_FAIL);
    }
    
}
