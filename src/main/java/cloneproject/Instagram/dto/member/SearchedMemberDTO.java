package cloneproject.Instagram.dto.member;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.vo.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchedMemberDTO {
    
    public String username;
    public String name;
    public Image image;
    public boolean isFollowing;
    public boolean isFollower;
    public boolean hasStory;

    @QueryProjection
    public SearchedMemberDTO(String username, String name, Image image, boolean isFollowing, boolean isFollower, boolean hasStory){
        this.username = username;
        this.name = name;
        this.image = image;
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.hasStory = hasStory;
    }

}

