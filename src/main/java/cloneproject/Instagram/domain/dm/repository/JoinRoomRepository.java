package cloneproject.Instagram.domain.dm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.JoinRoom;
import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.repository.jdbc.JoinRomRepositoryJdbc;
import cloneproject.Instagram.domain.dm.repository.querydsl.JoinRoomRepositoryQuerydsl;
import cloneproject.Instagram.domain.member.entity.Member;

public interface JoinRoomRepository
	extends JpaRepository<JoinRoom, Long>, JoinRoomRepositoryQuerydsl, JoinRomRepositoryJdbc {

	@Query(value = "select j from JoinRoom j join fetch j.member where j.room.id = :id")
	List<JoinRoom> findAllWithMemberByRoomId(@Param("id") Long id);

	Optional<JoinRoom> findByMemberAndRoom(Member member, Room room);

	List<JoinRoom> findByRoomAndMemberIn(Room room, List<Member> members);

	void deleteByMemberAndRoom(Member member, Room room);

	@Query(value = "select j from JoinRoom j join fetch j.message where j.room.id = :id")
	List<JoinRoom> findAllWithMessageByRoomId(@Param("id") Long id);

}
