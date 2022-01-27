package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
}
