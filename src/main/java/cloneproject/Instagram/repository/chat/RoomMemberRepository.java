package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.chat.RoomMember;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>, RoomMemberRepositoryJdbc {

    List<RoomMember> findAllByMemberIn(List<Member> members);

    List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);

    @Query("select r from RoomMember r join fetch r.member where r.room.id = :roomId")
    List<RoomMember> findAllWithMemberByRoomId(@Param("roomId") Long roomId);

    List<RoomMember> findAllByRoom(Room room);
}
