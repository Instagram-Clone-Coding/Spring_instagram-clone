package cloneproject.Instagram.dto.member;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.vo.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("유저 프로필 조회 응답 모델")
@Getter
@Setter
public class UserProfileResponse {
    
    @ApiModelProperty(value = "유저네임", example = "dlwlrma")
    String memberUsername;

    @ApiModelProperty(value = "이름", example = "이지금")
    String memberName;

    @ApiModelProperty(value = "웹사이트", example = "http://localhost:8080")
    String memberWebsite;

    @ApiModelProperty(value = "프로필사진")
    Image memberImage;

    @ApiModelProperty(value = "팔로잉 여부", example = "true")
    boolean isFollowing;

    @ApiModelProperty(value = "팔로워 여부", example = "false")
    boolean isFollower;

    @ApiModelProperty(value = "차단 여부", example = "false")
    boolean isBlocking;

    @ApiModelProperty(value = "차단당한 여부", example = "false")
    boolean isBlocked;

    @ApiModelProperty(value = "소개", example = "안녕하세요")
    String memberIntroduce;
    
    @ApiModelProperty(value = "포스팅 수", example = "90")
    Long memberPostsCount;

    @ApiModelProperty(value = "팔로잉 수", example = "100")
    Long memberFollowingsCount;

    @ApiModelProperty(value = "팔로워 수", example = "100")
    Long memberFollowersCount;

    @QueryProjection
    public UserProfileResponse(String username, String name, String website,Image image,boolean isFollowing, boolean isFollower, 
                                boolean isBlocking, boolean isBlocked, String introduce, Long postsCount,
                                Long followingsCount, Long followersCount){
        this.memberUsername = username;
        this.memberName = name;
        this.memberWebsite = website;
        this.memberImage = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.isBlocking = isBlocking;
        this.isBlocked = isBlocked;
        this.memberIntroduce = introduce;
        this.memberPostsCount = postsCount;
        this.memberFollowingsCount = followingsCount;
        this.memberFollowersCount = followersCount;

    }

    public void checkBlock(){
        if(this.isBlocked || this.isBlocking){
            this.memberPostsCount = 0L;
            this.memberFollowingsCount = 0L;
            this.memberFollowersCount = 0L;
        }
    }

}
