package cloneproject.Instagram.domain.feed.repository.querydsl;

import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.feed.dto.QPostLikeDto;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.QMember;
import cloneproject.Instagram.domain.member.dto.QLikeMemberDto;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static cloneproject.Instagram.domain.feed.entity.QCommentLike.commentLike;
import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;
import static cloneproject.Instagram.domain.member.entity.QMember.member;
import static cloneproject.Instagram.domain.feed.entity.QPostLike.postLike;;

@RequiredArgsConstructor
public class PostLikeRepositoryQuerydslImpl implements PostLikeRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<PostLikeDto> findAllPostLikeDtoOfFollowings(Long memberId, List<Long> postIds) {
		return queryFactory
			.select(new QPostLikeDto(
				postLike.post.id,
				postLike.member.username
			))
			.from(postLike)
			.innerJoin(postLike.member, member)
			.where(
				postLike.post.id.in(postIds)
					.and(postLike.member.in(getFollowings(memberId)))
			)
			.fetch();
	}

	private JPQLQuery<Member> getFollowings(Long memberId) {
		return JPAExpressions
			.select(follow.followMember)
			.from(follow)
			.innerJoin(follow.member, member)
			.innerJoin(follow.followMember, member)
			.where(follow.member.id.eq(memberId));
	}

	@Override
	public Page<LikeMemberDto> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId) {
		final List<LikeMemberDto> likeMembersDtos = queryFactory
			.select(new QLikeMemberDto(
				postLike.member,
				isFollowing(memberId, postLike.member),
				isFollower(memberId, postLike.member)
			))
			.from(postLike)
			.innerJoin(postLike.member, member)
			.where(
				postLike.post.id.eq(postId)
					.and(postLike.member.id.ne(memberId))
			)
			.orderBy(postLike.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		final long total = queryFactory
			.selectFrom(postLike)
			.where(
				postLike.post.id.eq(postId)
					.and(postLike.member.id.ne(memberId))
			)
			.fetchCount();

		return new PageImpl<>(likeMembersDtos, pageable, total);
	}

	@Override
	public Page<LikeMemberDto> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId) {
		final List<LikeMemberDto> likeMembersDtos = queryFactory
			.select(new QLikeMemberDto(
				commentLike.member,
				isFollowing(memberId, commentLike.member),
				isFollower(memberId, commentLike.member)
			))
			.from(commentLike)
			.innerJoin(commentLike.member, member)
			.where(
				commentLike.comment.id.eq(commentId)
					.and(commentLike.member.id.ne(memberId))
			)
			.orderBy(commentLike.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		final long total = queryFactory
			.selectFrom(commentLike)
			.where(
				commentLike.comment.id.eq(commentId)
					.and(commentLike.member.id.ne(memberId))
			)
			.fetchCount();

		return new PageImpl<>(likeMembersDtos, pageable, total);
	}

	private BooleanExpression isFollower(Long memberId, QMember member) {
		return JPAExpressions
			.selectFrom(follow)
			.where(
				follow.member.eq(member)
					.and(follow.followMember.id.eq(memberId))
			)
			.exists();
	}

	private BooleanExpression isFollowing(Long memberId, QMember member) {
		return JPAExpressions
			.selectFrom(follow)
			.where(
				follow.member.id.eq(memberId)
					.and(follow.followMember.eq(member))
			)
			.exists();
	}

}
