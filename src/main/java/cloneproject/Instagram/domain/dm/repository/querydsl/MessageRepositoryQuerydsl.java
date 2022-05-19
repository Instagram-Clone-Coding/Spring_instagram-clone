package cloneproject.Instagram.domain.dm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.dto.MessageDto;

public interface MessageRepositoryQuerydsl {

    Page<MessageDto> findMessageDtoPageByMemberIdAndRoomId(Long memberId, Long roomId, Pageable pageable);

}
