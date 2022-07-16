package cloneproject.Instagram.infra.kakao.exception;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.BusinessException;

public class KakaoMapApiFailException extends BusinessException {

	public KakaoMapApiFailException() {
		super(ErrorCode.KAKAO_MAP_API_FAIL);
	}

}
