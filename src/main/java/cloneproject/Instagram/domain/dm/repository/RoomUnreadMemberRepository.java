package cloneproject.Instagram.domain.dm.repository;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.entity.RoomUnreadMember;
import cloneproject.Instagram.domain.dm.repository.jdbc.RoomUnreadMemberRepositoryJdbc;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomUnreadMemberRepository extends JpaRepository<RoomUnreadMember, Long>, RoomUnreadMemberRepositoryJdbc {

    List<RoomUnreadMember> findAllByRoomAndMember(Room room, Member member);

    List<RoomUnreadMember> findAllByMessage(Message message);

    List<RoomUnreadMember> findAllByRoom(Room room);

}
