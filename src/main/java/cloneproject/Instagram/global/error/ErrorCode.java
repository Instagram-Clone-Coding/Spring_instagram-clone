package cloneproject.Instagram.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ErrorCode Convention
 * - 도메인 별로 나누어 관리
 * - [주체_이유] 형태로 생성
 * - 코드는 도메인명 앞에서부터 1~2글자로 사용
 * - 메시지는 "~~다."로 마무리
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

	// Global
	INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 오류입니다."),
	METHOD_NOT_ALLOWED(405, "G002", "허용되지 않은 HTTP method입니다."),
	INPUT_VALUE_INVALID(400, "G003", "유효하지 않은 입력입니다."),
	INPUT_TYPE_INVALID(400, "G004", "입력 타입이 유효하지 않습니다."),
	HTTP_MESSAGE_NOT_READABLE(400, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),
	HTTP_HEADER_INVALID(400, "G006", "request header가 유효하지 않습니다."),
	IMAGE_TYPE_NOT_SUPPORTED(400, "G007", "지원하지 않는 이미지 타입입니다."),
	FILE_CONVERT_FAIL(500, "G008", "변환할 수 없는 파일입니다."),
	ENTITY_TYPE_INVALID(500, "G009", "올바르지 않은 entity type 입니다."),
	FILTER_MUST_RESPOND(500, "G010", "필터에서 처리해야 할 요청이 Controller에 접근하였습니다."),

	// Member
	MEMBER_NOT_FOUND(400, "M001", "존재 하지 않는 유저입니다."),
	USERNAME_ALREADY_EXIST(400, "M002", "이미 존재하는 사용자 이름입니다."),
	AUTHENTICATION_FAIL(401, "M003", "로그인이 필요한 화면입니다."),
	AUTHORITY_INVALID(403, "M004", "권한이 없습니다."),
	ACCOUNT_MISMATCH(401, "M005", "계정 정보가 일치하지 않습니다."),
	EMAIL_NOT_CONFIRMED(400, "M007", "인증 이메일 전송을 먼저 해야합니다."),
	PASSWORD_RESET_FAIL(400, "M008", "잘못되거나 만료된 코드입니다."),
	BLOCK_ALREADY_EXIST(400, "M009", "이미 차단한 유저입니다."),
	UNBLOCK_FAIL(400, "M010", "차단하지 않은 유저는 차단해제 할 수 없습니다."),
	BLOCK_MYSELF_FAIL(400, "M011", "자기 자신을 차단 할 수 없습니다."),
	UNBLOCK_MYSELF_FAIL(400, "M012", "자기 자신을 차단해제 할 수 없습니다."),
	PASSWORD_EQUAL_WITH_OLD(400, "M013", "기존 비밀번호와 동일하게 변경할 수 없습니다."),
	LOGOUT_BY_ANOTHER(401, "M014", "다른 기기에 의해 로그아웃되었습니다."),

	// Follow
	FOLLOW_ALREADY_EXIST(400, "F001", "이미 팔로우한 유저입니다."),
	UNFOLLOW_FAIL(400, "F002", "팔로우하지 않은 유저는 언팔로우 할 수 없습니다."),
	FOLLOW_MYSELF_FAIL(400, "F003", "자기 자신을 팔로우 할 수 없습니다."),
	UNFOLLOW_MYSELF_FAIL(400, "F004", "자기 자신을 언팔로우 할 수 없습니다."),
	FOLLOWER_DELETE_FAIL(400, "F005", "팔로워 삭제할 수 없는 대상입니다."),

	// Jwt
	JWT_INVALID(401, "J001", "유효하지 않은 토큰입니다."),
	JWT_EXPIRED(401, "J002", "만료된 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(401, "J003", "만료된 REFRESH 토큰입니다. 재로그인 해주십시오."),

	// Feed
	POST_NOT_FOUND(400, "F001", "존재하지 않는 게시물입니다."),
	POST_CANT_DELETE(400, "F002", "게시물 게시자만 삭제할 수 있습니다."),
	POST_LIKE_NOT_FOUND(400, "F003", "해당 게시물에 좋아요를 누르지 않은 회원입니다."),
	POST_LIKE_ALREADY_EXIST(400, "F004", "해당 게시물에 이미 좋아요를 누른 회원입니다."),
	POST_IMAGES_AND_ALT_TEXTS_MISMATCH(400, "F005", "게시물 이미지 개수와 대체 텍스트 개수는 동일해야 합니다."),
	BOOKMARK_ALREADY_EXIST(400, "F006", "이미 해당 게시물을 저장하였습니다."),
	BOOKMARK_NOT_FOUND(400, "F007", "아직 해당 게시물을 저장하지 않았습니다."),
	COMMENT_NOT_FOUND(400, "F008", "존재하지 않는 댓글입니다."),
	COMMENT_CANT_DELETE(400, "F009", "타인이 작성한 댓글은 삭제할 수 없습니다."),
	COMMENT_LIKE_ALREADY_EXIST(400, "F010", "해당 댓글에 이미 좋아요를 누른 회원입니다."),
	COMMENT_LIKE_NOT_FOUND(400, "F011", "해당 댓글에 좋아요를 누르지 않은 회원입니다."),
	COMMENT_CANT_UPLOAD(400, "F012", "댓글 기능이 해제된 게시물에는 댓글을 작성할 수 없습니다."),
	REPLY_CANT_UPLOAD(400, "F013", "최상위 댓글에만 답글을 업로드할 수 있습니다."),
	POST_TAGS_EXCEED(400, "F014", "사용자 태그는 최대 20명까지 가능합니다."),

	// Chat
	CHAT_ROOM_NOT_FOUND(400, "C001", "존재하지 않는 채팅방입니다."),
	JOIN_ROOM_NOT_FOUND(400, "C002", "해당 채팅방에 참여하지 않은 회원입니다."),
	MESSAGE_IMAGE_INVALID(400, "C003", "메시지로 전송할 이미지는 필수입니다."),
	MESSAGE_NOT_FOUND(400, "C004", "존재하지 않는 메시지입니다."),
	MESSAGE_SENDER_MISMATCH(400, "C005", "해당 메시지를 전송한 회원이 아닙니다."),
	MESSAGE_LIKE_ALREADY_EXIST(400, "C006", "해당 메시지를 이미 좋아요한 회원입니다."),
	MESSAGE_LIKE_NOT_FOUND(400, "C007", "해당 메시지를 좋아요하지 않은 회원입니다."),

	// Alarm
	MISMATCHED_ALARM_TYPE(400, "A001", "알람 형식이 올바르지 않습니다."),

	// Email
	CANT_SEND_EMAIL(500, "E001", "이메일 전송 중 오류가 발생했습니다."),

	// HashTag
	HASHTAG_NOT_FOUND(400, "H001", "존재하지 않는 해시태그 입니다."),
	HASHTAG_FOLLOW_FAIL(400, "H002", "해시태그 팔로우에 실패했습니다."),
	HASHTAG_UNFOLLOW_FAIL(400, "H003", "해시태그 언팔로우에 실패했습니다."),
	HASHTAG_PREFIX_MISMATCH(400, "H004", "해시태그는 #으로 시작해야 합니다."),

	// Story
	INVALID_STORY_IMAGE(400, "S001", "스토리 이미지는 필수입니다."),
	INVALID_STORY_IMAGE_INDEX(400, "S002", "스토리 이미지 인덱스가 올바르지 않습니다."),
	MEMBER_STORY_NOT_FOUND(400, "S003", "해당 회원은 24시간 이내에 스토리를 업로드하지 않았습니다."),

	// Map api(kakao)
	KAKAO_MAP_API_FAIL(400, "K001", "카카오 맵 API 호출에 실패하였습니다."),

	;

	private final int status;
	private final String code;
	private final String message;

}
