package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.JoinRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long>, JoinRoomRepositoryQuerydsl {
    Optional<JoinRoom> findByMemberIdAndRoomId(Long memberId, Long roomId);
    void deleteByMemberIdAndRoomId(Long memberId, Long roomId);
}
