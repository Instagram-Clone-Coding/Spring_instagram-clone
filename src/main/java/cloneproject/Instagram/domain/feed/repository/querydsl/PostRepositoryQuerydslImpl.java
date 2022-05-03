package cloneproject.Instagram.domain.feed.repository.querydsl;

import cloneproject.Instagram.domain.feed.dto.PostDTO;
import cloneproject.Instagram.domain.feed.dto.PostResponse;
import cloneproject.Instagram.domain.feed.dto.*;
import cloneproject.Instagram.domain.member.entity.QMember;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;
import static cloneproject.Instagram.domain.feed.entity.QBookmark.bookmark;
import static cloneproject.Instagram.domain.feed.entity.QPost.post;
import static cloneproject.Instagram.domain.feed.entity.QPostLike.postLike;

@RequiredArgsConstructor
public class PostRepositoryQuerydslImpl implements PostRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<PostDTO> findPostDtoPage(Long memberId, Pageable pageable) {
		final List<PostDTO> postDTOs = queryFactory
			.select(new QPostDTO(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag
			))
			.from(post)
			.innerJoin(post.member, QMember.member)
			.where(isPostUploadedByFollowings(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(post)
			.where(isPostUploadedByFollowings(memberId))
			.fetchCount();

		return new PageImpl<>(postDTOs, pageable, total);
	}

	@Override
	public Optional<PostResponse> findPostResponse(Long postId, Long memberId) {
		return Optional.ofNullable(queryFactory
			.select(new QPostResponse(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag
			))
			.from(post)
			.where(post.id.eq(postId))
			.fetchOne());
	}

	@Override
	public Page<PostDTO> findPostDtoPage(Pageable pageable, Long memberId, List<Long> postIds) {
		final List<PostDTO> postDTOs = getPostDTOsByPostIdIn(memberId, postIds);
		final long total = getTotalByPostIdIn(postIds);

		return new PageImpl<>(postDTOs, pageable, total);
	}

	private List<PostDTO> getPostDTOsByPostIdIn(Long memberId, List<Long> postIds) {
		return queryFactory
			.select(new QPostDTO(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag
			))
			.from(post)
			.innerJoin(post.member, QMember.member)
			.where(post.id.in(postIds))
			.orderBy(post.id.desc())
			.fetch();
	}

	private long getTotalByPostIdIn(List<Long> ids) {
		return queryFactory
				.selectFrom(post)
				.where(post.id.in(ids))
				.fetchCount();
	}

	private BooleanExpression isExistPostLikeWherePostEqAndMemberIdEq(Long id) {
		return JPAExpressions
			.selectFrom(postLike)
			.where(postLike.post.eq(post).and(postLike.member.id.eq(id)))
			.exists();
	}

	private BooleanExpression isExistBookmarkWherePostEqMemberIdEq(Long id) {
		return JPAExpressions
			.selectFrom(bookmark)
			.where(bookmark.post.eq(post).and(bookmark.member.id.eq(id)))
			.exists();
	}

	private BooleanExpression isPostUploadedByFollowings(Long id) {
		return post.member.username.in(
			JPAExpressions
				.select(follow.followMember.username)
				.from(follow)
				.where(follow.member.id.eq(id)));
	}
}
