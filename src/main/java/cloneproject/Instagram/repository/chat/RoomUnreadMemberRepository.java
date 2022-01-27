package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.RoomUnreadMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomUnreadMemberRepository extends JpaRepository<RoomUnreadMember, Long> {
    void deleteByRoomIdAndMemberId(Long roomId, Long memberId);
    Optional<RoomUnreadMember> findByRoomIdAndMemberId(Long roomId, Long memberId);
    long countByRoomId(Long roomId);
}
