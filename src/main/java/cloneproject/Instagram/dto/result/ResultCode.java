package cloneproject.Instagram.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    // Member
    REGISTER_SUCCESS(200, "M001", "회원가입 되었습니다."),
    LOGIN_SUCCESS(200, "M002", "로그인 되었습니다."),
    REISSUE_SUCCESS(200, "M003", "재발급 되었습니다."),
    GET_USERPROFILE_SUCCESS(200, "M004", "회원 프로필 조회 완료"),
    GET_MINIPROFILE_SUCCESS(200, "M005", "미니 프로필 조회 완료"),
    UPLOAD_MEMBER_IMAGE_SUCCESS(200, "M006", "회원 이미지 등록 완료"),
    DELETE_MEMBER_IMAGE_SUCCESS(200, "M007", "회원 이미지 삭제 완료"),
    GET_EDIT_PROFILE_SUCCESS(200, "M008", "회원 프로필 수정정보 조회"),
    EDIT_PROFILE_SUCCESS(200, "M009", "회원 프로필 수정 완료"),
    UPDATE_PASSWORD_SUCCESS(200, "M010", "회원 비밀번호 변경 완료"),
    CHECK_USERNAME_GOOD(200, "M011", "사용가능한 username"),
    CHECK_USERNAME_BAD(200, "M012", "사용불가능한 username"),
    CONFIRM_EMAIL_FAIL(200, "M013", "이메일 인증을 완료할 수 없습니다. 인증코드를 다시확인 해주세요"),
    SEND_CONFIRM_EMAIL_SUCCESS(200, "M014", "인증코드 이메일 전송 완료"),
    SEARCH_MEMBER_SUCCESS(200, "M015", "회원 검색 완료"),
    GET_MENU_MEMBER_SUCCESS(200, "M016", "상단 메뉴 프로필 조회 완료"),
    SEND_RESET_PASSWORD_EMAIL_SUCCESS(200, "M017", "비밀번호 재설정 메일 전송 완료"),
    RESET_PASSWORD_SUCCESS(200, "M018", "비밀번호 재설정 완료"),
    LOGIN_WITH_CODE_SUCCESS(200, "M019", "비밀번호 재설정 코드로 로그인 성공"),
    EXPIRE_RESET_PASSWORD_CODE_SUCCESS(200, "M020", "비밀번호 재설정 코드 만료 성공"),
    
    // Alarm
    GET_ALARMS_SUCCESS(200, "A001", "알림 조회 완료"),

    //Follow
    FOLLOW_SUCCESS(200, "F001", "회원 팔로우 완료"),
    UNFOLLOW_SUCCESS(200, "F002", "회원 언팔로우 완료"),
    GET_FOLLOWINGS_SUCCESS(200, "F003", "회원 팔로우 목록"),
    GET_FOLLOWERS_SUCCESS(200, "F004", "회원 팔로워 목록"),

    //Block
    BLOCK_SUCCESS(200, "B001", "회원 차단 완료"),
    UNBLOCK_SUCCESS(200, "B002", "회원 차단해제 완료"),

    // MemberPost
    FIND_RECENT15_MEMBER_POSTS_SUCCESS(200, "MP001", "회원의 최근 게시물 15개 조회 성공"),
    FIND_MEMBER_POSTS_SUCCESS(200, "MP002", "회원의 게시물 조회 성공"),
    FIND_RECENT15_MEMBER_SAVED_POSTS_SUCCESS(200, "MP003", "회원의 최근 저장한 게시물 15개 조회 성공"),
    FIND_MEMBER_SAVED_POSTS_SUCCESS(200, "MP004", "회원의 저장한 게시물 조회 성공"),
    FIND_RECENT15_MEMBER_TAGGED_POSTS_SUCCESS(200, "MP005", "회원의 최근 태그된 게시물 15개 조회 성공"),
    FIND_MEMBER_TAGGED_POSTS_SUCCESS(200, "MP006", "회원의 태그된 게시물 조회 성공"),


    // POST
    CREATE_POST_SUCCESS(200, "P001", "게시물 생성 성공"),
    UPLOAD_POST_IMAGES_SUCCESS(200, "P002", "게시물 이미지 업로드 성공"),
    ADD_POST_IMAGE_TAGS_SUCCESS(200, "P003", "게시물 이미지 태그 추가 성공"),
    FIND_POST_PAGE_SUCCESS(200, "P004", "게시물 페이지 조회 성공"),
    DELETE_POST_SUCCESS(200, "P005", "게시물 삭제 성공"),
    FIND_POST_SUCCESS(200, "P006", "게시물 조회 성공"),
    FIND_RECENT10POSTS_SUCCESS(200, "P007", "최근 게시물 10개 조회 성공"),
    LIKE_POST_SUCCESS(200, "P008", "게시물 좋아요 성공"),
    UNLIKE_POST_SUCCESS(200, "P009", "게시물 좋아요 취소 성공"),
    SAVE_POST_SUCCESS(200, "P010", "게시물 저장 성공"),
    UNSAVE_POST_SUCCESS(200, "P011", "게시물 저장 취소 성공"),
    CREATE_COMMENT_SUCCESS(200, "P012", "게시물 댓글 생성 성공"),
    DELETE_COMMENT_SUCCESS(200, "P012", "게시물 댓글 삭제 성공"),
    GET_COMMENT_PAGE_SUCCESS(200, "P013", "게시물 댓글 페이지 조회 성공"),
    GET_REPLY_PAGE_SUCCESS(200, "P014", "게시물 답글 페이지 조회 성공"),
    GET_POST_LIKES_SUCCESS(200, "P015", "게시물 좋아요한 사람 목록 페이지 조회 성공"),
    LIKE_COMMENT_SUCCESS(200, "P016", "댓글 좋아요 성공"),
    UNLIKE_COMMENT_SUCCESS(200, "P017", "댓글 좋아요 취소 성공"),
    GET_COMMENT_LIKES_SUCCESS(200, "P018", "댓글 좋아요한 사람 목록 페이지 조회 성공"),
    SHARE_POST_SUCCESS(200, "P019", "게시물 DM 공유 성공"),

    // CHAT
    CREATE_CHAT_ROOM_SUCCESS(200, "C001", "채팅방 생성 요청 성공"),
    INQUIRE_CHAT_ROOM_SUCCESS(200, "C002", "채팅방 조회 성공"),
    DELETE_JOIN_ROOM_SUCCESS(200, "C003", "참여 중인 채팅방 삭제 성공"),
    GET_JOIN_ROOMS_SUCCESS(200, "C004", "채팅방 목록 조회 성공"),
    GET_CHAT_MESSAGES_SUCCESS(200, "C005", "채팅 메시지 목록 조회 성공"),
    SEND_IMAGE_SUCCESS(200, "C006", "이미지 전송 성공"),
    
    // Hashtag
    GET_HASHTAG_POSTS_SUCCESS(200, "H001", "해시태그 게시물 목록 페이징 조회 성공"),
    GET_HASHTAGS_SUCCESS(200, "H002", "해시태그 목록 페이징 조회 성공"),
    ;

    private int status;
    private final String code;
    private final String message;
}