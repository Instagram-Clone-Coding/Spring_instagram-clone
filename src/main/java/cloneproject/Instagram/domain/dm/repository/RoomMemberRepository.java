package cloneproject.Instagram.domain.dm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.entity.RoomMember;
import cloneproject.Instagram.domain.dm.repository.jdbc.RoomMemberRepositoryJdbc;
import cloneproject.Instagram.domain.member.entity.Member;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>, RoomMemberRepositoryJdbc {

	List<RoomMember> findAllByMemberIn(List<Member> members);

	List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);

	@Query("select rm from RoomMember rm join fetch rm.member where rm.room.id = :roomId")
	List<RoomMember> findAllWithMemberByRoomId(@Param("roomId") Long roomId);

	@Query("select rm from RoomMember rm join fetch rm.member where rm.room.id in :roomIds")
	List<RoomMember> findAllWithMemberByRoomIdIn(@Param("roomIds") List<Long> roomIds);

	List<RoomMember> findAllByRoom(Room room);

}
