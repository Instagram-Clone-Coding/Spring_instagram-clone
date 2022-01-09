package cloneproject.Instagram.exception;

import cloneproject.Instagram.dto.error.ErrorCode;

public class UploadProfileImageFailException extends BusinessException{
    public UploadProfileImageFailException() {
        super(ErrorCode.UPLOAD_PROFILE_IMAGE_FAIL);
    }
    
}
