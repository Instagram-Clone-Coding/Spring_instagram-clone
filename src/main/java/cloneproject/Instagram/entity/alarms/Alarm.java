package cloneproject.Instagram.entity.alarms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "alarms")
public class Alarm {
    
    @Id
    @Column(name = "alarm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alarm_type")
    @Enumerated(EnumType.STRING)
    private AlarmType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_agent_id")
    private Member agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_target_id")
    private Member target;

    @Column(name = "alarm_item_id")
    private Long itemId;

    @CreatedDate
    @Column(name = "alarm_created_at")
    private Date createdAt;
    
    @Builder
    public Alarm(AlarmType type, Member agent, Member target, Long itemId){
        this.type = type;
        this.agent = agent;
        this.target = target;
        this.itemId = itemId;
    }

}
