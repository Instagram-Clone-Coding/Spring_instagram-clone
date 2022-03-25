package cloneproject.Instagram.domain.dm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.dto.JoinRoomDTO;

public interface JoinRoomRepositoryQuerydsl {
    Page<JoinRoomDTO> findJoinRoomDTOPageByMemberId(Long memberId, Pageable pageable);
}
