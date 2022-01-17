package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.vo.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@ApiModel("유저 미니프로필 응답 모델")
@Getter
@Builder
@AllArgsConstructor
public class MiniProfileResponse {

    @ApiModelProperty(value = "유저네임", example = "dlwlrma")
    String memberUsername;

    @ApiModelProperty(value = "프로필사진")
    Image memberImage;

    @ApiModelProperty(value = "이름", example = "이지금")
    String memberName;

    @ApiModelProperty(value = "웹사이트", example = "http://localhost:8080")
    String memberWebsite;

    @ApiModelProperty(value = "팔로잉 여부", example = "true")
    boolean isFollowing;

    @ApiModelProperty(value = "팔로워 여부", example = "false")
    boolean isFollower;

    @ApiModelProperty(value = "포스팅 수", example = "90")
    Integer memberPostCount;

    @ApiModelProperty(value = "팔로워 수", example = "100")
    Integer memberFollowersCount;

    @ApiModelProperty(value = "팔로잉 수", example = "100")
    Integer memberFollowingsCount;

    
}
