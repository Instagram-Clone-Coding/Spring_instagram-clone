package cloneproject.Instagram.entity.chat;

import cloneproject.Instagram.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_room_id")
    private JoinRoom joinRoom;

    @Lob
    @Column(name = "message_content")
    private String content;

    @CreatedDate
    @Column(name = "message_created_date")
    private LocalDateTime createdDate;

    @Column(name = "message_type")
    private MessageType type;

    @Builder
    public Message(Member member, JoinRoom joinRoom, String content, MessageType type) {
        this.member = member;
        this.joinRoom = joinRoom;
        this.content = content;
        this.type = type;
    }
}
