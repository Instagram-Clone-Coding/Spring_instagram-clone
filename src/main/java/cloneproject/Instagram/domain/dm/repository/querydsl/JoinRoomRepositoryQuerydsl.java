package cloneproject.Instagram.domain.dm.repository.querydsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.dto.JoinRoomDto;
import cloneproject.Instagram.domain.dm.entity.JoinRoom;

public interface JoinRoomRepositoryQuerydsl {

	Optional<JoinRoom> findWithRoomAndMemberByMemberIdAndRoomId(Long memberId, Long roomId);

	List<JoinRoom> findAllWithMessageByMemberIdAndRoomIdIn(Long memberId, List<Long> roomIds);

	Page<JoinRoomDto> findJoinRoomDtoPageByMemberId(Long memberId, Pageable pageable);

}
