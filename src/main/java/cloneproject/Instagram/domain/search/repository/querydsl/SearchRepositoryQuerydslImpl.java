package cloneproject.Instagram.domain.search.repository.querydsl;

import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.hashtag.entity.QHashtag.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;
import static cloneproject.Instagram.domain.search.entity.QSearch.*;
import static cloneproject.Instagram.domain.search.entity.QSearchHashtag.*;
import static cloneproject.Instagram.domain.search.entity.QSearchMember.*;

import java.util.List;
import java.util.Map;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.search.dto.QSearchHashtagDTO;
import cloneproject.Instagram.domain.search.dto.QSearchMemberDTO;
import cloneproject.Instagram.domain.search.dto.SearchHashtagDto;
import cloneproject.Instagram.domain.search.dto.SearchMemberDto;
import cloneproject.Instagram.domain.search.entity.Search;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SearchRepositoryQuerydslImpl implements SearchRepositoryQuerydsl {

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
						.or(searchMember.member.name.like(keyword)))
			).or(search.id.in(
				JPAExpressions
					.select(searchHashtag.id)
					.from(searchHashtag)
					.innerJoin(searchHashtag.hashtag, hashtag)
					.where(searchHashtag.hashtag.name.like(keyword))
			)))
			.orderBy(search.count.desc())
			.limit(50)
			.distinct()
			.fetch();
	}

	@Override
	public List<Search> findHashtagsByTextLike(String text) {
		final String keyword = text + "%";

		return queryFactory
			.select(search)
			.from(search)
			.where(search.id.in(
				JPAExpressions
					.select(searchHashtag.id)
					.from(searchHashtag)
					.innerJoin(searchHashtag.hashtag, hashtag)
					.where(searchHashtag.hashtag.name.like(keyword))
			))
			.orderBy(search.count.desc())
			.limit(50)
			.distinct()
			.fetch();
	}

	@Override
	public void checkMatchingMember(Long loginId, String text, List<Search> searches, List<Long> searchIds) {
		final Search matchingSearch = queryFactory
			.select(searchMember._super)
			.from(searchMember)
			.where(searchMember.member.username.eq(text))
			.fetchOne();
		if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
			searches.add(0, matchingSearch);
			searchIds.add(0, matchingSearch.getId());
			log.warn(searches.get(searches.size() - 1).getId().toString());
			log.warn(searchIds.get(searchIds.size() - 1).toString());
			searches.remove(searches.size() - 1);
			searchIds.remove(searchIds.size() - 1);
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
			log.warn(searches.get(searches.size() - 1).getId().toString());
			log.warn(searchIds.get(searchIds.size() - 1).toString());
			searches.remove(searches.size() - 1);
			searchIds.remove(searchIds.size() - 1);
		}
	}

	@Override
	public Map<Long, SearchHashtagDto> findAllSearchHashtagDtoByIdIn(List<Long> searchIds) {
		return queryFactory
			.from(searchHashtag)
			.innerJoin(searchHashtag.hashtag, hashtag)
			.where(searchHashtag.id.in(searchIds))
			.transform(GroupBy.groupBy(searchHashtag.id).as(new QSearchHashtagDTO(
					searchHashtag.dtype,
					searchHashtag.hashtag
				))
			);
	}

	@Override
	public Map<Long, SearchMemberDto> findAllSearchMemberDtoByIdIn(Long loginId, List<Long> searchIds) {
		return queryFactory
			.from(searchMember)
			.innerJoin(searchMember.member, member)
			.where(searchMember.id.in(searchIds))
			.transform(GroupBy.groupBy(searchMember.id).as(new QSearchMemberDTO(
						searchMember._super.dtype,
						searchMember.member,
						JPAExpressions
							.selectFrom(follow)
							.where(follow.member.id.eq(loginId).and(follow.followMember.id.eq(searchMember.member.id)))
							.exists(),
						JPAExpressions
							.selectFrom(follow)
							.where(follow.member.id.eq(searchMember.member.id).and(follow.followMember.id.eq(loginId)))
							.exists()
					)
				)
			);
	}

}
