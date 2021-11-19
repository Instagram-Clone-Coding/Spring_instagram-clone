package cloneproject.Instagram.vo;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class UsernameWithImage {
    
    public String username;
    public Image image;

}
