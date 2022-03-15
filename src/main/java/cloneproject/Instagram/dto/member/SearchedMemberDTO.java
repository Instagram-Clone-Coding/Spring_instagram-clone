package cloneproject.Instagram.dto.member;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.vo.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchedMemberDTO {

    private String username;
    private String name;
    private Image image;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean hasStory;
    private List<FollowDTO> followingMemberFollow; // 내 팔로잉 중 해당 멤버를 팔로우 하고있는 사람

    @QueryProjection
    public SearchedMemberDTO(String username, String name, Image image, boolean isFollowing, boolean isFollower) {
        this.username = username;
        this.name = name;
        this.image = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.hasStory = false;
    }

    public void setFollowingMemberFollow(List<FollowDTO> followingMemberFollow) {
        this.followingMemberFollow = followingMemberFollow;
    }
}

