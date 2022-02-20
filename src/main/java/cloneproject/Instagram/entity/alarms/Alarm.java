package cloneproject.Instagram.entity.alarms;

import java.time.LocalDateTime;

import javax.persistence.*;

import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.post.Post;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id")
    private Follow follow;

    @CreatedDate
    @Column(name = "alarm_created_date")
    private LocalDateTime createdDate;
    
    @Builder
    public Alarm(AlarmType type, Member agent, Member target, Post post, Comment comment, Follow follow) {
        this.type = type;
        this.agent = agent;
        this.target = target;
        this.post = post;
        this.comment = comment;
        this.follow = follow;
    }
}
