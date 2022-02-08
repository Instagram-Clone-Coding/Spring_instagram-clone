package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.JoinRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long>, JoinRoomRepositoryQuerydsl {
    Optional<JoinRoom> findByMemberIdAndRoomId(Long memberId, Long roomId);
    void deleteByMemberIdAndRoomId(Long memberId, Long roomId);
    @Query(value = "select j from JoinRoom j join fetch j.member where j.room.id = :id")
    List<JoinRoom> findAllByRoomId(@Param("id") Long id);
}
