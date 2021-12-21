package cloneproject.Instagram.vo;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class FollowerInfo {
    
    public String username;
    public String name;
    public Image image;
    // 변수명이 isFollowing인 경우 response에 following이 자동으로 추가됨.
    // is를 없애면 해결되나, 바꾼경우 변수명이 직관적이지 않아서 그대로 유지 
    public boolean isFollowing;
    public boolean hasStory;

}
