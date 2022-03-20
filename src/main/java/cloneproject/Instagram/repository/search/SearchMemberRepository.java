package cloneproject.Instagram.repository.search;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.search.SearchMember;

public interface SearchMemberRepository extends JpaRepository<SearchMember, Long>{
    
    Optional<SearchMember> findByMemberUsername(String username);

}
