package cloneproject.Instagram.dto.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.dto.post.MiniProfilePostDTO;
import cloneproject.Instagram.dto.post.PostImageDTO;
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

    @ApiModelProperty(value = "차단 여부", example = "false")
    boolean isBlocking;

    @ApiModelProperty(value = "차단당한 여부", example = "false")
    boolean isBlocked;

    @ApiModelProperty(value = "포스팅 수", example = "90")
    Long memberPostsCount;

    @ApiModelProperty(value = "팔로워 수", example = "100")
    Long memberFollowersCount;

    @ApiModelProperty(value = "팔로잉 수", example = "100")
    Long memberFollowingsCount;

    @ApiModelProperty(value = "최근 게시물 3개")
    List<MiniProfilePostDTO> memberPosts;
    
    @QueryProjection
    public MiniProfileResponse(String username, String name, Image image,boolean isFollowing, boolean isFollower, 
                                boolean isBlocking, boolean isBlocked, Long postsCount,
                                Long followingsCount, Long followersCount){
        this.memberUsername = username;
        this.memberName = name;
        this.memberImage = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.isBlocking = isBlocking;
        this.isBlocked = isBlocked;
        this.memberPostsCount = postsCount;
        this.memberFollowingsCount = followingsCount;
        this.memberFollowersCount = followersCount;

    }

    public void setMemberPosts(List<PostImageDTO> postImageDTOs){
        final Map<Long, List<PostImageDTO>> postDTOMap = postImageDTOs.stream()
                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
        List<MiniProfilePostDTO> results = new ArrayList<MiniProfilePostDTO>();
        postDTOMap.forEach((id, p) -> results.add(
            MiniProfilePostDTO.builder()
                        .postId(id)
                        .postImageUrl(p.get(0).getPostImageUrl())
                        .build()
        ));
        this.memberPosts = results;
    }

    public void checkBlock(){
        if(this.isBlocked || this.isBlocking){
            this.memberPostsCount = 0L;
            this.memberFollowersCount = 0L;
            this.memberFollowingsCount = 0L;
            this.memberPosts = null;
        }
    }
    
}
