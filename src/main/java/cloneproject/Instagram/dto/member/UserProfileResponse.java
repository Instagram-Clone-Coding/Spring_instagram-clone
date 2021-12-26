package cloneproject.Instagram.dto.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@ApiModel("유저 프로필 조회 응답 모델")
@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    @ApiModelProperty(value = "유저네임", example = "dlwlrma")
    String memberUsername;
    @ApiModelProperty(value = "이름", example = "이지금")
    String memberName;
    @ApiModelProperty(value = "프로필사진 URL", example = "https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing")
    String memberImageUrl;
    @ApiModelProperty(value = "팔로워 수", example = "100")
    Integer memberFollowers;
    @ApiModelProperty(value = "팔로잉 수", example = "100")
    Integer memberFollowings;
}
