package cloneproject.Instagram.domain.feed.repository.querydsl;

import static cloneproject.Instagram.domain.feed.entity.QBookmark.*;
import static cloneproject.Instagram.domain.feed.entity.QPost.*;
import static cloneproject.Instagram.domain.feed.entity.QPostLike.*;
import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.follow.entity.QHashtagFollow.*;
import static cloneproject.Instagram.domain.hashtag.entity.QHashtagPost.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.feed.dto.PostDto;
import cloneproject.Instagram.domain.feed.dto.QPostDto;

@RequiredArgsConstructor
public class PostRepositoryQuerydslImpl implements PostRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<PostDto> findPostDtoPage(Long memberId, Pageable pageable) {
		final List<PostDto> postDtos = queryFactory
			.select(new QPostDto(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag,
				post.likeFlag
			))
			.from(post)
			.innerJoin(post.member, member)
			.where(post.member.id.in(getFollowingMemberIdsByMemberId(memberId))
				.or(post.id.in(getPostIdsOfFollowingHashtagByMemberId(memberId))))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(post)
			.innerJoin(post.member, member)
			.where(post.member.id.in(getFollowingMemberIdsByMemberId(memberId))
				.or(post.id.in(getPostIdsOfFollowingHashtagByMemberId(memberId))))
			.fetchCount();

		return new PageImpl<>(postDtos, pageable, total);
	}

	@Override
	public Optional<PostDto> findPostDto(Long postId, Long memberId) {
		return Optional.ofNullable(queryFactory
			.select(new QPostDto(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag,
				post.likeFlag
			))
			.from(post)
			.where(post.id.eq(postId))
			.fetchOne());
	}

	@Override
	public Optional<PostDto> findPostDtoWithoutLogin(Long postId) {
		return Optional.ofNullable(queryFactory
			.select(new QPostDto(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				post.commentFlag,
				post.likeFlag
			))
			.from(post)
			.where(post.id.eq(postId))
			.fetchOne());
	}

	@Override
	public Page<PostDto> findPostDtoPage(Pageable pageable, Long memberId, List<Long> postIds) {
		final List<PostDto> postDtos = queryFactory
			.select(new QPostDto(
				post.id,
				post.content,
				post.uploadDate,
				post.member,
				post.comments.size(),
				post.postLikes.size(),
				isExistBookmarkWherePostEqMemberIdEq(memberId),
				isExistPostLikeWherePostEqAndMemberIdEq(memberId),
				post.commentFlag,
				post.likeFlag
			))
			.from(post)
			.innerJoin(post.member, member)
			.where(post.id.in(postIds))
			.orderBy(post.id.desc())
			.fetch();

		final long total = queryFactory
			.selectFrom(post)
			.where(post.id.in(postIds))
			.fetchCount();

		return new PageImpl<>(postDtos, pageable, total);
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

	private JPQLQuery<Long> getFollowingMemberIdsByMemberId(Long memberId) {
		return JPAExpressions
			.select(follow.followMember.id)
			.from(follow)
			.where(follow.member.id.eq(memberId));
	}

	private JPQLQuery<Long> getPostIdsOfFollowingHashtagByMemberId(Long memberId) {
		return JPAExpressions
			.select(hashtagPost.post.id)
			.from(hashtagPost)
			.join(hashtagFollow).on(hashtagFollow.member.id.eq(memberId)
				.and(hashtagFollow.hashtag.id.eq(hashtagPost.hashtag.id)))
			.innerJoin(hashtagPost.post, post).on(hashtagPost.post.member.id.ne(memberId));
	}

}
