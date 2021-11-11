package cloneproject.Instagram.vo;

import lombok.*;
import net.bytebuddy.asm.Advice.Local;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import javax.persistence.*;

@Getter
@Embeddable
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    private String value;

    private LocalDateTime createdAt;
    
    public void updateTokenValue(String value){
        this.value = value;
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public RefreshToken(String value){
        updateTokenValue(value);
    }

}
