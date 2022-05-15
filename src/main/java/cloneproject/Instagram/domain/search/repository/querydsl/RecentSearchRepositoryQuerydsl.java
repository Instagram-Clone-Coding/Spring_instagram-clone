package cloneproject.Instagram.domain.search.repository.querydsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.search.entity.RecentSearch;
import cloneproject.Instagram.domain.search.entity.Search;

public interface RecentSearchRepositoryQuerydsl {
	Optional<RecentSearch> findRecentSearchByUsername(Long loginId, String username);
	Optional<RecentSearch> findRecentSearchByHashtagName(Long loginId, String name);

	List<Search> findAllByMemberId(Long loginId, Pageable pageable);
	Long getRecentSearchCount(Long loginId);

}
