package cloneproject.Instagram.domain.member.repository.querydsl;

import lombok.RequiredArgsConstructor;

import com.querydsl.jpa.impl.JPAQueryFactory;

import static cloneproject.Instagram.domain.member.entity.QBlock.block;

@RequiredArgsConstructor
public class BlockRepositoryQuerydslImpl implements BlockRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isBlockingOrIsBlocked(Long loginUserId, Long targetMemberId) {
		final Integer result = queryFactory
			.selectOne()
			.from(block)
			.where((block.member.id.eq(loginUserId).and(block.blockMember.id.eq(targetMemberId)))
				.or(block.member.id.eq(targetMemberId).and(block.blockMember.id.eq(loginUserId))))
			.fetchFirst();

		return result != null;
	}

}
