package cloneproject.Instagram.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "U001", "내부 서버 오류입니다."),
    INVALID_INPUT_VALUE(400, "U002", "유효하지 않은 입력입니다."),
    METHOD_NOT_ALLOWED(405, "U003", "허용되지 않은 HTTP method입니다."),
    INVALID_TYPE_VALUE(400, "U004", "입력 타입이 유효하지 않습니다."),
    ;

    private int status;
    private final String code;
    private final String message;
}
