package cloneproject.Instagram.domain.member.entity.redis;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash("ResetPasswordCode")
public class ResetPasswordCode implements Serializable{
    
    @Id
    @Indexed // Redis 에선 Indexed 어노테이션이 있어야 find 가능
    private String username;

    private String code;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long timeout = 1800L; // 30분

    @Builder
    public ResetPasswordCode(String username, String code) {
        this.username = username;
        this.code = code;
    }
}
