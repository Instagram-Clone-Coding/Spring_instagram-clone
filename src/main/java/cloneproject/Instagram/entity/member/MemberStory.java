package cloneproject.Instagram.entity.member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@RedisHash(value = "memberStories", timeToLive = 86400)
public class MemberStory {

    @Id
    private Long memberId;
    private List<Long> storyIds;

    @Builder
    public MemberStory(Long memberId, List<Long> storyIds) {
        this.memberId = memberId;
        this.storyIds = storyIds;
    }
}
