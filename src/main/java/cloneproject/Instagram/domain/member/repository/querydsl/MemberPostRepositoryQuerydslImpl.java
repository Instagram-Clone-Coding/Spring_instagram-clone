package cloneproject.Instagram.domain.member.repository.querydsl;

import static cloneproject.Instagram.domain.feed.entity.QBookmark.*;
import static cloneproject.Instagram.domain.feed.entity.QPost.*;
import static cloneproject.Instagram.domain.feed.entity.QPostLike.*;
import static cloneproject.Instagram.domain.feed.entity.QPostTag.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.feed.dto.QMemberPostDto;
import cloneproject.Instagram.domain.member.entity.Member;

@RequiredArgsConstructor
public class MemberPostRepositoryQuerydslImpl implements MemberPostRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<MemberPostDto> findMemberPostDtos(Long loginMemberId, String username, Pageable pageable) {
		final List<MemberPostDto> posts = queryFactory
			.select(new QMemberPostDto(
				post.id,
				post.member,
				post.postImages.size().gt(1),
				post.likeFlag,
				isExistPostLikeWherePostEqAndMemberUsernameEq(loginMemberId),
				post.comments.size(),
				post.postLikes.size()))
			.from(post)
			.where(post.member.username.eq(username))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(post)
			.where(post.member.username.eq(username))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

	@Override
	public Page<MemberPostDto> findMemberSavedPostDtos(Long loginMemberId, Pageable pageable) {
		final List<MemberPostDto> posts = queryFactory
			.select(new QMemberPostDto(
				bookmark.post.id,
				bookmark.post.member,
				bookmark.post.postImages.size().gt(1),
				bookmark.post.comments.size(),
				bookmark.post.postLikes.size()))
			.from(bookmark)
			.where(bookmark.member.id.eq(loginMemberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(bookmark.post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(bookmark)
			.where(bookmark.member.id.eq(loginMemberId))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

	@Override
	public Page<MemberPostDto> findMemberTaggedPostDtos(Long loginMemberId, String username, Pageable pageable) {
		final List<MemberPostDto> posts = queryFactory
			.select(new QMemberPostDto(
				postTag.postImage.post.id,
				postTag.postImage.post.member,
				postTag.postImage.post.postImages.size().gt(1),
				postTag.postImage.post.likeFlag,
				isExistPostLikeWherePostEqAndMemberUsernameEq(loginMemberId),
				postTag.postImage.post.comments.size(),
				postTag.postImage.post.postLikes.size()))
			.from(postTag)
			.where(postTag.tag.username.eq(username))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(postTag.postImage.post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(postTag)
			.where(postTag.tag.username.eq(username))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

	private BooleanExpression isExistPostLikeWherePostEqAndMemberUsernameEq(Long id) {
		return JPAExpressions
			.selectFrom(postLike)
			.where(postLike.post.eq(post).and(postLike.member.id.eq(id)))
			.exists();
	}

}
