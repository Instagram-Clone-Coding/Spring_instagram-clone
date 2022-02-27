package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.chat.RoomUnreadMember;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomUnreadMemberRepository extends JpaRepository<RoomUnreadMember, Long>, RoomUnreadMemberRepositoryJdbc {

    void deleteByRoomIdAndMemberId(Long roomId, Long memberId);

    Optional<RoomUnreadMember> findByRoomIdAndMemberId(Long roomId, Long memberId);

    long countByRoomId(Long roomId);

    Optional<RoomUnreadMember> findByRoomAndMember(Room room, Member member);

    List<RoomUnreadMember> findByRoomAndMemberIn(Room room, List<Member> members);
}
