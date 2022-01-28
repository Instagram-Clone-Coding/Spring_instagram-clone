package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.JoinRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JoinRoomRepositoryQuerydsl {
    Page<JoinRoomDTO> findJoinRoomDTOPagebyMemberId(Long memberId, Pageable pageable);
}
