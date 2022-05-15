package cloneproject.Instagram.domain.search.repository.querydsl;

import static cloneproject.Instagram.domain.search.entity.QRecentSearch.*;
import static cloneproject.Instagram.domain.search.entity.QSearchMember.*;
import static cloneproject.Instagram.domain.search.entity.QSearchHashtag.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.search.entity.RecentSearch;
import cloneproject.Instagram.domain.search.entity.Search;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RecentSearchRepositoryQuerydslImpl implements RecentSearchRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<RecentSearch> findRecentSearchByUsername(Long loginId, String username) {
		return Optional.ofNullable(queryFactory
			.select(recentSearch)
			.from(recentSearch)
			.innerJoin(recentSearch.search, searchMember._super)
			.where(recentSearch.member.id.eq(loginId).and(searchMember.member.username.eq(username)))
			.fetchOne());
	}

	@Override
	public Optional<RecentSearch> findRecentSearchByHashtagName(Long loginId, String name) {
		return Optional.ofNullable(queryFactory
			.select(recentSearch)
			.from(recentSearch)
			.innerJoin(recentSearch.search, searchHashtag._super)
			.where(recentSearch.member.id.eq(loginId).and(searchHashtag.hashtag.name.eq(name)))
			.fetchOne());
	}

	@Override
	public List<Search> findAllByMemberId(Long loginId, Pageable pageable) {
		return queryFactory
			.select(recentSearch.search)
			.from(recentSearch)
			.where(recentSearch.member.id.eq(loginId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(recentSearch.lastSearchedAt.desc())
			.distinct()
			.fetch();
	}

	@Override
	public Long getRecentSearchCount(Long loginId) {
		return queryFactory
			.selectOne()
			.from(recentSearch)
			.where(recentSearch.member.id.eq(loginId))
			.fetchCount();
	}

}
