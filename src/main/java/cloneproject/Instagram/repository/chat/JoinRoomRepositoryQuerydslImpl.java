package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.dto.chat.JoinRoomDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class JoinRoomRepositoryQuerydslImpl implements JoinRoomRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JoinRoomDTO> findJoinRoomDTOPagebyMemberId(Long memberId, Pageable pageable) {

        return new PageImpl<>(null);
    }
}
