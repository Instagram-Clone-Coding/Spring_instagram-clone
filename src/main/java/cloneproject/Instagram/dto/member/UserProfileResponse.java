package cloneproject.Instagram.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    String memberUsername;
    String memberName;
    String memberImageUrl;
    Long memberFollowers;
    Long memberFollowings;
}
