package cloneproject.Instagram.domain.search.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.search.entity.RecentSearch;
import cloneproject.Instagram.domain.search.repository.querydsl.RecentSearchRepositoryQuerydsl;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long>, RecentSearchRepositoryQuerydsl {

	Optional<RecentSearch> findByMemberIdAndSearchId(Long memberId, Long searchId);

	void deleteAllByMemberId(Long memberId);

}
