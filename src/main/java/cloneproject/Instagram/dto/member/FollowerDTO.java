package cloneproject.Instagram.dto.member;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.vo.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @AllArgsConstructor
@NoArgsConstructor
public class FollowerDTO {
    
    public String username;
    public String name;
    public Image image;
    public boolean isFollowing;
    public boolean isFollower;
    public boolean hasStory;
    public boolean isMe;

    @QueryProjection
    public FollowerDTO(String username, String name, Image image, boolean isFollowing, boolean isFollower, boolean hasStory, boolean isMe){
        this.username = username;
        this.name = name;
        this.image = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.hasStory = hasStory;
        this.isMe = isMe;
    }

}
