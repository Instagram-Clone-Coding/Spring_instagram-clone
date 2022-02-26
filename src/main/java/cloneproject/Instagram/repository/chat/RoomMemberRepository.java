package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.RoomMember;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>, RoomMemberRepositoryJdbc {

    List<RoomMember> findAllByRoomId(Long roomId);

    List<RoomMember> findAllByMemberIn(List<Member> members);

    List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);
}
