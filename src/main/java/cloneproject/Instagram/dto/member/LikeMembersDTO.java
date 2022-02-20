package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.vo.Image;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeMembersDTO {

    private String username;
    private String name;
    private Image image;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean hasStory;

    @QueryProjection
    public LikeMembersDTO(String username, String name, Image image, boolean isFollowing, boolean isFollower, boolean hasStory) {
        this.username = username;
        this.name = name;
        this.image = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.hasStory = hasStory;
    }
}
