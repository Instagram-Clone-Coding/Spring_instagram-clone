package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.chat.RoomUnreadMember;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomUnreadMemberRepository extends JpaRepository<RoomUnreadMember, Long>, RoomUnreadMemberRepositoryJdbc {

    List<RoomUnreadMember> findAllByRoomAndMember(Room room, Member member);

    List<RoomUnreadMember> findAllByMessage(Message message);

    List<RoomUnreadMember> findAllByRoom(Room room);
}
