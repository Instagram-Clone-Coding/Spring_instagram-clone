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

    // Member
    MEMBER_DOES_NOT_EXIST(401, "M001", "존재 하지 않는 유저입니다."),
    USERNAME_ALREADY_EXISTS(401, "M002", "이미 존재하는 사용자 이름입니다."),
    NEED_LOGIN(401, "M003", "로그인이 필요한 화면입니다."),
    NO_AUTHORITY(403, "M004", "권한이 없습니다."),
    ACCOUNT_DOES_NOT_MATCH(403, "M005", "계정정보가 일치하지 않습니다."),
    
    // FOLLOW
    ALREADY_FOLLOW(401, "F001", "이미 팔로우한 유저입니다."),
    CANT_UNFOLLOW(401, "F002", "팔로우하지 않을 유저는 언팔로우 할 수 없습니다"),
    CANT_FOLLOW_MYSELF(401, "F003", "자기자신을 팔로우 할 수 없습니다"),
    CANT_UNFOLLOW_MYSELF(401, "F004", "자기자신을 언팔로우 할 수 없습니다"),

    // Jwt
    INVALID_JWT(401, "J001", "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(401, "J002", "만료된 ACCESS 토큰입니다. REISSUE 해주십시오."),
    EXPIRED_REFRESH_TOKEN(401, "J003", "만료된 REFRESH 토큰입니다. 재로그인 해주십시오."),

    // POST
    POST_NOT_FOUND(400, "P001", "존재하지 않는 게시물입니다."),
    NO_POST_IMAGE(400, "P002", "게시물 이미지는 필수입니다."),
    NOT_SUPPORTED_IMAGE_TYPE(400, "P003", "지원하지 않는 이미지 타입입니다."),
    POST_IMAGE_NOT_FOUND(400, "P004", "존재하지 않는 게시물 이미지입니다."),
    NO_POST_IMAGE_TAG(400, "P005", "게시물 이미지 태그는 필수입니다."),
    INVALID_TAG_LOCATION(400, "P006", "게시물 이미지 태그 위치가 유효하지 않습니다."),
    ;

    private int status;
    private final String code;
    private final String message;
}
