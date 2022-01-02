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
    UPLOAD_MEMBER_IMAGE_SUCCESS(200, "M005", "회원 이미지 등록 완료"),

    //Follow
    FOLLOW_SUCCESS(200, "F001", "회원 팔로우 완료"),
    UNFOLLOW_SUCCESS(200, "F002", "회원 언팔로우 완료"),
    GET_FOLLOWINGS_SUCCESS(200, "F003", "회원 팔로우 목록"),
    GET_FOLLOWERS_SUCCESS(200, "F004", "회원 팔로워 목록"),

    // POST
    CREATE_POST_SUCCESS(200, "P001", "게시물 생성 성공"),
    UPLOAD_POST_IMAGES_SUCCESS(200, "P002", "게시물 이미지 업로드 성공"),
    ADD_POST_IMAGE_TAGS_SUCCESS(200, "P003", "게시물 이미지 태그 추가 성공"),
    FIND_POST_PAGE_SUCCESS(200, "P004", "게시물 페이지 조회 성공"),
    DELETE_POST_SUCCESS(200, "P005", "게시물 삭제 성공"),
    ;

    private int status;
    private final String code;
    private final String message;
}