package cloneproject.Instagram.domain.dm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.dto.JoinRoomDto;

public interface JoinRoomRepositoryQuerydsl {

    Page<JoinRoomDto> findJoinRoomDtoPageByMemberId(Long memberId, Pageable pageable);

}
