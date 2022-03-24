package cloneproject.Instagram.domain.dm.repository;

import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.entity.RoomMember;
import cloneproject.Instagram.domain.dm.repository.jdbc.RoomMemberRepositoryJdbc;
import cloneproject.Instagram.domain.member.entity.Member;

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
