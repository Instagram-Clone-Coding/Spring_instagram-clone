package cloneproject.Instagram.entity.member;

import lombok.*;

import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.querydsl.core.annotations.QueryProjection;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token_value", nullable = false, unique = true)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @LastModifiedDate
    @Column(name = "refresh_token_last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "refresh_token_location")
    private String location;

    @Column(name = "refresh_token_device")
    private String device;

    @Column(name = "refresh_token_deleted", nullable = false)
    private boolean deleted;

    public void updateToken(String newValue){
        this.value = newValue;       
    }

    public void deleteToken(){
        deleted = true;        
    }

    @QueryProjection
    @Builder
    public RefreshToken(Member member, String value, String location, String device){
        this.member = member;
        this.value = value;
        this.location = location;
        this.device = device;
        this.deleted = false;
    }

}
