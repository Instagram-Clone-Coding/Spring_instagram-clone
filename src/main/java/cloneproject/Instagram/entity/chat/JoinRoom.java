package cloneproject.Instagram.entity.chat;

import cloneproject.Instagram.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "join_rooms")
public class JoinRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_room_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "join_flag")
    private boolean isJoin;

    @Column(name = "read_flag")
    private boolean isRead;

    @Builder
    public JoinRoom(Room room, Member member) {
        this.room = room;
        this.member = member;
        this.isJoin = true;
        this.isRead = true;
    }
}
