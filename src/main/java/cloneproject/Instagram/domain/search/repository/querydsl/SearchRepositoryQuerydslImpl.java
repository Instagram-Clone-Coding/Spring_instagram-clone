package cloneproject.Instagram.domain.search.repository.querydsl;

import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.hashtag.entity.QHashtag.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;
import static cloneproject.Instagram.domain.search.entity.QSearch.*;
import static cloneproject.Instagram.domain.search.entity.QSearchHashtag.*;
import static cloneproject.Instagram.domain.search.entity.QSearchMember.*;
import static cloneproject.Instagram.domain.feed.entity.QPost.*;

import java.util.List;
import java.util.Map;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.search.dto.QSearchHashtagDto;
import cloneproject.Instagram.domain.search.dto.QSearchMemberDto;
import cloneproject.Instagram.domain.search.dto.SearchHashtagDto;
import cloneproject.Instagram.domain.search.dto.SearchMemberDto;
import cloneproject.Instagram.domain.search.entity.Search;
import cloneproject.Instagram.domain.search.entity.SearchHashtag;
import cloneproject.Instagram.domain.search.entity.SearchMember;

@Slf4j
@RequiredArgsConstructor
public class SearchRepositoryQuerydslImpl implements SearchRepositoryQuerydsl {

	private static final int SEARCH_SIZE = 50;
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Search> findAllByTextLike(String text) {
		final String keyword = text + "%";

		return queryFactory
			.select(search)
			.from(search)
			.where(search.id.in(
					JPAExpressions
						.select(searchMember.id)
						.from(searchMember)
						.innerJoin(searchMember.member, member)
						.where(searchMember.member.username.like(keyword)
							.or(searchMember.member.name.like(keyword))))
				.or(search.id.in(
					JPAExpressions
						.select(searchHashtag.id)
						.from(searchHashtag)
						.innerJoin(searchHashtag.hashtag, hashtag)
						.where(searchHashtag.hashtag.name.like(keyword)))))
			.orderBy(search.count.desc())
			.limit(SEARCH_SIZE)
			.distinct()
			.fetch();
	}

	@Override
	public List<Long> findMemberIdsByTextLike(String text) {
		final String keyword = text + "%";

		return queryFactory
			.select(searchMember.member.id)
			.from(searchMember)
			.where(searchMember.member.username.like(keyword))
			.orderBy(searchMember.count.desc())
			.limit(SEARCH_SIZE)
			.distinct()
			.fetch();
	}

	@Override
	public List<Long> findHashtagIdsByTextLike(String text) {
		final String keyword = text + "%";

		return queryFactory
			.select(searchHashtag.hashtag.id)
			.from(searchHashtag)
			.where(searchHashtag.hashtag.name.like(keyword))
			.orderBy(searchHashtag.count.desc())
			.limit(SEARCH_SIZE)
			.distinct()
			.fetch();
	}

	@Override
	public List<Long> findMemberIdsOrderByPostCounts() {
		return queryFactory
			.select(post.member.id)
			.from(post)
			.groupBy(post.member.id)
			.orderBy(post.count().desc())
			.distinct()
			.limit(SEARCH_SIZE)
			.fetch();
	}

	@Override
	public List<Search> findHashtagsByTextLike(String text) {
		final String keyword = text + "%";

		return queryFactory
			.select(searchHashtag._super)
			.from(searchHashtag)
			.where(searchHashtag.hashtag.name.like(keyword))
			.orderBy(searchHashtag.count.desc())
			.limit(SEARCH_SIZE)
			.distinct()
			.fetch();
	}

	@Override
	public void checkMatchingMember(String text, List<Long> memberIds) {
		final SearchMember matchingSearch = queryFactory
			.select(searchMember)
			.from(searchMember)
			.where(searchMember.member.username.eq(text))
			.fetchOne();
		if (matchingSearch != null && !memberIds.contains(matchingSearch.getMember().getId())) {
			memberIds.add(0, matchingSearch.getMember().getId());
			checkSearchSize(memberIds);
		}
	}

	@Override
	public void checkMatchingHashtag(String text, List<Long> hashtagIds) {
		final SearchHashtag matchingSearch = queryFactory
			.select(searchHashtag)
			.from(searchHashtag)
			.where(searchHashtag.hashtag.name.eq(text))
			.fetchOne();
		if (matchingSearch != null && !hashtagIds.contains(matchingSearch.getHashtag().getId())) {
			hashtagIds.add(0, matchingSearch.getHashtag().getId());
			checkSearchSize(hashtagIds);
		}
	}

	@Override
	public void checkMatchingMember(String text, List<Search> searches, List<Long> searchIds) {
		final Search matchingSearch = queryFactory
			.select(searchMember._super)
			.from(searchMember)
			.where(searchMember.member.username.eq(text))
			.fetchOne();
		if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
			searches.add(0, matchingSearch);
			searchIds.add(0, matchingSearch.getId());
			checkSearchSize(searches);
			checkSearchSize(searchIds);
		}
	}

	@Override
	public void checkMatchingHashtag(String text, List<Search> searches, List<Long> searchIds) {
		final Search matchingSearch = queryFactory
			.select(searchHashtag._super)
			.from(searchHashtag)
			.where(searchHashtag.hashtag.name.eq(text))
			.fetchOne();
		if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
			searches.add(0, matchingSearch);
			searchIds.add(0, matchingSearch.getId());
			checkSearchSize(searches);
			checkSearchSize(searchIds);
		}
	}

	@Override
	public Map<Long, SearchHashtagDto> findAllSearchHashtagDtoByIdIn(List<Long> searchIds) {
		return queryFactory
			.from(searchHashtag)
			.innerJoin(searchHashtag.hashtag, hashtag)
			.where(searchHashtag.id.in(searchIds))
			.transform(GroupBy.groupBy(searchHashtag.id).as(new QSearchHashtagDto(
				searchHashtag.dtype,
				searchHashtag.hashtag)));
	}

	@Override
	public Map<Long, SearchMemberDto> findAllSearchMemberDtoByIdIn(Long loginId, List<Long> searchIds) {
		return queryFactory
			.from(searchMember)
			.innerJoin(searchMember.member, member)
			.where(searchMember.id.in(searchIds))
			.transform(GroupBy.groupBy(searchMember.id).as(new QSearchMemberDto(
				searchMember._super.dtype,
				searchMember.member,
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.id.eq(loginId).and(follow.followMember.id.eq(searchMember.member.id)))
					.exists(),
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.id.eq(searchMember.member.id).and(follow.followMember.id.eq(loginId)))
					.exists())));
	}

	private <T> void checkSearchSize(List<T> list) {
		while (list.size() > SEARCH_SIZE) {
			list.remove(list.size() - 1);
		}
	}

}
