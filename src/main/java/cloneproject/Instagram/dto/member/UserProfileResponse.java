package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.vo.Image;
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

    @ApiModelProperty(value = "프로필사진")
    Image memberImage;

    @ApiModelProperty(value = "팔로잉 여부", example = "true")
    boolean isFollowing;

    @ApiModelProperty(value = "팔로워 여부", example = "false")
    boolean isFollower;

    @ApiModelProperty(value = "소개", example = "안녕하세요")
    String memberIntroduce;

    @ApiModelProperty(value = "팔로워 수", example = "100")
    Integer memberFollowersCount;

    @ApiModelProperty(value = "팔로잉 수", example = "100")
    Integer memberFollowingsCount;

}
