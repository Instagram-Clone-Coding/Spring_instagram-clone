package cloneproject.Instagram.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowDTO {
    
    public String memberUsername;

    @JsonIgnore
    public String followMemberUsername;

    @QueryProjection
    public FollowDTO(String memberUsername, String followMemberUsername){
        this.memberUsername = memberUsername;
        this.followMemberUsername = followMemberUsername;
    }

}
