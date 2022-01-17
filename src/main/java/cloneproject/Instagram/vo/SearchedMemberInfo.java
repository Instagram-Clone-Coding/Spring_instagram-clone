package cloneproject.Instagram.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchedMemberInfo {
    
    String username;
    String name;
    Image image;
    boolean isFollwing;

}
