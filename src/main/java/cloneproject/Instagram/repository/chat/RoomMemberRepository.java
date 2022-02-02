package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    List<RoomMember> findAllByMemberId(Long memberId);
}
