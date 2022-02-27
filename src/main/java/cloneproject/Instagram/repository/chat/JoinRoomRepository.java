package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.JoinRoom;
import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long>, JoinRoomRepositoryQuerydsl, JoinRomRepositoryJdbc {

    @Query(value = "select j from JoinRoom j join fetch j.member where j.room.id = :id")
    List<JoinRoom> findAllByRoomId(@Param("id") Long id);

    Optional<JoinRoom> findByMemberAndRoom(Member member, Room room);

    List<JoinRoom> findByRoomAndMemberIn(Room room, List<Member> members);

    List<JoinRoom> findAllByRoom(Room room);

    void deleteByMemberAndRoom(Member member, Room room);
}
