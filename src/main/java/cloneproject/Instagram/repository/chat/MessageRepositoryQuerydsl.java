package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepositoryQuerydsl {

    Page<MessageDTO> findMessageDTOPageByMemberIdAndRoomId(Long memberId, Long roomId, Pageable pageable);
}
