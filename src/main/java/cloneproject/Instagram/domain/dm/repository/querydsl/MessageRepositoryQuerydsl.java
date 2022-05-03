package cloneproject.Instagram.domain.dm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.dto.MessageDTO;

public interface MessageRepositoryQuerydsl {

    Page<MessageDTO> findMessageDTOPageByMemberIdAndRoomId(Long memberId, Long roomId, Pageable pageable);
}
