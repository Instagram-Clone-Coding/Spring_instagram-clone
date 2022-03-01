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
    MEMBER_DOES_NOT_EXIST(400, "M001", "존재 하지 않는 유저입니다."),
    USERNAME_ALREADY_EXISTS(400, "M002", "이미 존재하는 사용자 이름입니다."),
    NEED_LOGIN(401, "M003", "로그인이 필요한 화면입니다."),
    NO_AUTHORITY(403, "M004", "권한이 없습니다."),
    ACCOUNT_DOES_NOT_MATCH(401, "M005", "계정정보가 일치하지 않습니다."),
    UPLOAD_PROFILE_IMAGE_FAIL(400, "M006", "회원 이미지를 업로드 하는 중 실패했습니다."),
    NO_CONFIRM_EMAIL(400, "M007", "인증 이메일 전송을 먼저 해야합니다."),
    
    // FOLLOW
    ALREADY_FOLLOW(400, "F001", "이미 팔로우한 유저입니다."),
    CANT_UNFOLLOW(400, "F002", "팔로우하지 않을 유저는 언팔로우 할 수 없습니다"),
    CANT_FOLLOW_MYSELF(400, "F003", "자기자신을 팔로우 할 수 없습니다"),
    CANT_UNFOLLOW_MYSELF(400, "F004", "자기자신을 언팔로우 할 수 없습니다"),

    // BLOCK
    ALREADY_BLOCK(400, "B001", "이미 차단한 유저입니다."),
    CANT_UNBLOCK(400, "B002", "차단하지 않을 유저는 차단해제 할 수 없습니다"),
    CANT_BLOCK_MYSELF(400, "B003", "자기자신을 차단 할 수 없습니다"),
    CANT_UNBLOCK_MYSELF(400, "B004", "자기자신을 차단해제 할 수 없습니다"),

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
    INVALID_POST_IMAGE_TAG(400, "P006", "게시물 이미지 태그가 유효하지 않습니다."),
    INVALID_TAG_POSITION(400, "P007", "태그 좌표는 0 ~ 100 사이로 입력해주세요."),
    INVALID_IMAGE_SEQUENCE(400, "P008", "이미지 순번이 유효하지 않습니다."),
    INVALID_POST_IMAGE(400, "P009", "게시물 이미지는 필수입니다."),
    POST_LIKE_NOT_FOUND(400, "P010", "해당 게시물에 좋아요를 누르지 않은 회원입니다."),
    POST_LIKE_ALREADY_EXIST(400, "P011", "해당 게시물에 이미 좋아요를 누른 회원입니다."),
    BOOKMARK_ALREADY_EXIST(400, "P012", "이미 해당 게시물을 저장하였습니다."),
    BOOKMARK_NOT_FOUND(400, "P013", "아직 해당 게시물을 저장하지 않았습니다."),
    COMMENT_NOT_FOUND(400, "P014", "존재하지 않는 댓글입니다."),
    COMMENT_CANT_DELETE(400, "P015", "타인이 작성한 댓글은 삭제할 수 없습니다."),
    COMMENT_LIKE_ALREADY_EXIST(400, "P016", "해당 댓글에 이미 좋아요를 누른 회원입니다."),
    COMMENT_LIKE_NOT_FOUND(400, "P017", "해당 댓글에 좋아요를 누르지 않은 회원입니다."),
    CANT_COMMENT_CREATE(400, "P018", "댓글 기능이 해제된 게시물에는 댓글을 작성할 수 없습니다."),
    POST_IMAGES_AND_ALT_TEXTS_MISMATCH(400, "P019", "게시물 이미지마다 대체 텍스트를 추가해주세요."),

    // Chat
    CHAT_ROOM_NOT_FOUND(400, "C001", "존재하지 않는 채팅방입니다."),
    JOIN_ROOM_NOT_FOUND(400, "C002", "해당 채팅방에 참여하지 않은 회원입니다."),
    MESSAGE_IMAGE_INVALID(400, "C003", "메시지로 전송할 이미지는 필수입니다."),
    MESSAGE_NOT_FOUND(400, "C004", "존재하지 않는 메시지입니다."),
    MESSAGE_SENDER_MISMATCH(400, "C005", "해당 메시지를 전송한 회원이 아닙니다."),
    MESSAGE_LIKE_ALREADY_EXIST(400, "C006", "해당 메시지를 이미 좋아요한 회원입니다."),
    MESSAGE_LIKE_NOT_FOUND(400, "C007", "해당 메시지를 좋아요하지 않은 회원입니다."),

    // FILE
    CANT_CONVERT_FILE(500, "FI001", "파일을 변환할수 없습니다"),

    // Alarm
    MISMATCHED_ALARM_TYPE(400, "A001", "알람 형식이 올바르지 않습니다.")
    ;
    ;

    private int status;
    private final String code;
    private final String message;
}
