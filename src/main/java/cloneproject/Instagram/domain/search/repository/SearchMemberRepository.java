package cloneproject.Instagram.domain.search.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.search.entity.SearchMember;

public interface SearchMemberRepository extends JpaRepository<SearchMember, Long> {

	Optional<SearchMember> findByMemberUsername(String username);

}
