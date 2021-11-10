package cloneproject.Instagram.entity.auth;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EnableJpaAuditing
@Table(name = "refresh_tokens")
public class RefreshToken {
    
    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", unique = true)
    private String memberId;

    @Column(name = "refresh_token_value")
    private String tokenValue;

    @CreatedDate
    @Column(name = "refresh_token_created_at")
    private LocalDateTime createdAt;
    

    public void updateTokenValue(String tokenValue){
        this.tokenValue = tokenValue;
    }

    @Builder
    public RefreshToken(String memberId, String tokenValue){
        this.memberId = memberId;
        this.tokenValue = tokenValue;
    }

}
