package cloneproject.Instagram.domain.dm.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import cloneproject.Instagram.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "room")
    private List<RoomUnreadMember> roomUnreadMembers = new ArrayList<>();

    @OneToMany(mappedBy = "room")
    private List<RoomMember> roomMembers = new ArrayList<>();

    public Room(Member member) {
        this.member = member;
    }

}
