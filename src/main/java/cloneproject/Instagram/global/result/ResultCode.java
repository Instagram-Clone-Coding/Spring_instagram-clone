package cloneproject.Instagram.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ResultCode Convention
 * - 도메인 별로 나누어 관리
 * - [동사_목적어_SUCCESS] 형태로 생성
 * - 코드는 도메인명 앞에서부터 1~2글자로 사용
 * - 메시지는 "~~다."로 마무리
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

	// Member
	REGISTER_SUCCESS(200, "M001", "회원가입에 성공하였습니다."),
	LOGIN_SUCCESS(200, "M002", "로그인에 성공하였습니다."),
	REISSUE_SUCCESS(200, "M003", "재발급에 성공하였습니다."),
	GET_USERPROFILE_SUCCESS(200, "M004", "회원 프로필을 조회하였습니다."),
	GET_MINIPROFILE_SUCCESS(200, "M005", "미니 프로필을 조회하였습니다."),
	UPLOAD_MEMBER_IMAGE_SUCCESS(200, "M006", "회원 이미지를 등록하였습니다."),
	DELETE_MEMBER_IMAGE_SUCCESS(200, "M007", "회원 이미지를 삭제하였습니다."),
	GET_EDIT_PROFILE_SUCCESS(200, "M008", "회원 프로필 수정정보를 조회하였습니다."),
	EDIT_PROFILE_SUCCESS(200, "M009", "회원 프로필을 수정하였습니다."),
	UPDATE_PASSWORD_SUCCESS(200, "M010", "회원 비밀번호를 변경하였습니다."),
	CHECK_USERNAME_GOOD(200, "M011", "사용가능한 username 입니다."),
	CHECK_USERNAME_BAD(200, "M012", "사용불가능한 username 입니다."),
	CONFIRM_EMAIL_FAIL(200, "M013", "이메일 인증을 완료할 수 없습니다."),
	SEND_CONFIRM_EMAIL_SUCCESS(200, "M014", "인증코드 이메일을 전송하였습니다."), // 결번 M015
	GET_MENU_MEMBER_SUCCESS(200, "M016", "상단 메뉴 프로필을 조회하였습니다."),
	SEND_RESET_PASSWORD_EMAIL_SUCCESS(200, "M017", "비밀번호 재설정 메일을 전송했습니다."),
	RESET_PASSWORD_SUCCESS(200, "M018", "비밀번호 재설정에 성공했습니다."),
	LOGIN_WITH_CODE_SUCCESS(200, "M019", "비밀번호 재설정 코드로 로그인 했습니다."),
	LOGOUT_SUCCESS(200, "M020", "로그아웃하였습니다."),
	CHECK_RESET_PASSWORD_CODE_GOOD(200, "M021", "올바른 비밀번호 재설정 코드입니다."),
	CHECK_RESET_PASSWORD_CODE_BAD(200, "M022", "올바르지 않은 비밀번호 재설정 코드입니다."),
	LOGOUT_BY_ANOTHER_DEVICE(200, "M023", "다른 기기에 의해 로그아웃되었습니다."),
	GET_LOGIN_DEVICES_SUCCESS(200, "M024", "로그인 한 기기들을 조회하였습니다."),
	LOGOUT_DEVICE_SUCCESS(200, "M025", "해당 기기를 로그아웃 시켰습니다."),

	// Alarm
	GET_ALARMS_SUCCESS(200, "A001", "알림 조회에 성공하였습니다."),

	// Follow
	FOLLOW_SUCCESS(200, "F001", "회원 팔로우를 성공하였습니다."),
	UNFOLLOW_SUCCESS(200, "F002", "회원 언팔로우를 성공하였습니다."),
	GET_FOLLOWINGS_SUCCESS(200, "F003", "회원 팔로잉 목록을 조회하였습니다."),
	GET_FOLLOWERS_SUCCESS(200, "F004", "회원 팔로워 목록을 조회하였습니다."),
	DELETE_FOLLOWER_SUCCESS(200, "F005", "팔로워 삭제를 성공하였습니다."),

	// Block
	BLOCK_SUCCESS(200, "B001", "회원 차단 완료"),
	UNBLOCK_SUCCESS(200, "B002", "회원 차단해제 완료"),

	// MemberPost
	GET_RECENT15_MEMBER_POSTS_SUCCESS(200, "MP001", "회원의 최근 게시물 15개 조회에 성공하였습니다."),
	GET_MEMBER_POSTS_SUCCESS(200, "MP002", "회원의 게시물 조회에 성공하였습니다."),
	GET_RECENT15_MEMBER_SAVED_POSTS_SUCCESS(200, "MP003", "회원의 최근 저장한 게시물 15개 조회에 성공하였습니다."),
	GET_MEMBER_SAVED_POSTS_SUCCESS(200, "MP004", "회원의 저장한 게시물 조회에 성공하였습니다."),
	GET_RECENT15_MEMBER_TAGGED_POSTS_SUCCESS(200, "MP005", "회원의 최근 태그된 게시물 15개 조회에 성공하였습니다."),
	GET_MEMBER_TAGGED_POSTS_SUCCESS(200, "MP006", "회원의 태그된 게시물 조회에 성공하였습니다."),

	// Feed
	CREATE_POST_SUCCESS(200, "F001", "게시물 업로드에 성공하였습니다."),
	DELETE_POST_SUCCESS(200, "F002", "게시물 삭제에 성공하였습니다."),
	FIND_POST_PAGE_SUCCESS(200, "F003", "게시물 목록 페이지 조회에 성공하였습니다."),
	FIND_POST_SUCCESS(200, "F004", "게시물 조회에 성공하였습니다."),
	FIND_RECENT10POSTS_SUCCESS(200, "F005", "최근 게시물 10개 조회에 성공하였습니다."),
	LIKE_POST_SUCCESS(200, "F006", "게시물 좋아요에 성공하였습니다."),
	UN_LIKE_POST_SUCCESS(200, "F007", "게시물 좋아요 해제에 성공하였습니다."),
	BOOKMARK_POST_SUCCESS(200, "F008", "게시물 북마크에 성공하였습니다."),
	UN_BOOKMARK_POST_SUCCESS(200, "F009", "게시물 북마크 해제에 성공하였습니다."),
	CREATE_COMMENT_SUCCESS(200, "F010", "댓글 업로드에 성공하였습니다."),
	DELETE_COMMENT_SUCCESS(200, "F011", "댓글 삭제에 성공하였습니다."),
	GET_COMMENT_PAGE_SUCCESS(200, "F012", "댓글 목록 페이지 조회에 성공하였습니다."),
	GET_REPLY_PAGE_SUCCESS(200, "F013", "답글 목록 페이지 조회에 성공하였습니다."),
	GET_POST_LIKES_SUCCESS(200, "F014", "게시물에 좋아요한 회원 목록 페이지 조회에 성공하였습니다."),
	LIKE_COMMENT_SUCCESS(200, "F015", "댓글 좋아요에 성공하였습니다."),
	UNLIKE_COMMENT_SUCCESS(200, "F016", "댓글 좋아요 해제에 성공하였습니다."),
	GET_COMMENT_LIKES_SUCCESS(200, "F017", "댓글에 좋아요한 회원 목록 페이지 조회에 성공하였습니다."),
	SHARE_POST_SUCCESS(200, "F018", "게시물 DM 공유에 성공하였습니다."),
	GET_HASHTAG_POSTS_SUCCESS(200, "F019", "해시태그 게시물 목록 페이징 조회 성공"),

	// Chat
	CREATE_CHAT_ROOM_SUCCESS(200, "C001", "채팅방 생성에 성공하였습니다."),
	INQUIRE_CHAT_ROOM_SUCCESS(200, "C002", "채팅방 조회에 성공하였습니다."),
	DELETE_JOIN_ROOM_SUCCESS(200, "C003", "채팅방 나가기에 성공하였습니다."),
	GET_JOIN_ROOMS_SUCCESS(200, "C004", "채팅방 목록 조회에 성공하였습니다."),
	GET_CHAT_MESSAGES_SUCCESS(200, "C005", "채팅 메시지 목록 조회에 성공하였습니다."),
	SEND_IMAGE_SUCCESS(200, "C006", "이미지 전송 성공"),

	// Hashtag
	GET_HASHTAGS_SUCCESS(200, "H001", "해시태그 목록 페이징 조회에 성공하였습니다."),
	FOLLOW_HASHTAG_SUCCESS(200, "H002", "해시태그 팔로우에 성공하였습니다."),
	UNFOLLOW_HASHTAG_SUCCESS(200, "H003", "해시태그 언팔로우에 성공하였습니다."),
	GET_HASHTAG_PROFILE_SUCCESS(200, "H004", "해시태그 프로필 조회에 성공하였습니다."),

	// Story
	CREATE_STORY_SUCCESS(200, "S001", "스토리 업로드 성공"),
	GET_STORY_SUCCESS(200, "S002", "스토리 조회 성공"),

	// Search
	SEARCH_SUCCESS(200, "SE001", "검색에 성공하였습니다."),
	MARK_SEARCHED_ENTITY_SUCCESS(200, "SE002", "검색 조회수 증가, 최근검색기록 업데이트에 성공하였습니다."),
	GET_TOP_15_RECENT_SEARCH_SUCCESS(200, "SE003", "최근 검색 기록 15개 조회에 성공하였습니다."),
	GET_RECENT_SEARCH_SUCCESS(200, "SE004", "최근 검색 기록 페이지(무한스크롤) 조회에 성공하였습니다."),
	DELETE_RECENT_SEARCH_SUCCESS(200, "SE005", "최근 검색 기록 삭제에 성공하였습니다."),
	DELETE_ALL_RECENT_SEARCH_SUCCESS(200, "SE006", "최근 검색 기록 전체 삭제에 성공하였습니다."),
	GET_MEMBER_AUTO_COMPLETE_SUCCESS(200, "SE007", "멤버 자동완성 조회에 성공하였습니다."),
	GET_HASHTAG_AUTO_COMPLETE_SUCCESS(200, "SE008", "해시태그 자동완성 조회에 성공하였습니다.");

	private final int status;
	private final String code;
	private final String message;

}