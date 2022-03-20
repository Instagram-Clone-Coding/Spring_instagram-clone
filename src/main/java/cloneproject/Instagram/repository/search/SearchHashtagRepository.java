package cloneproject.Instagram.repository.search;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.search.SearchHashtag;

public interface SearchHashtagRepository extends JpaRepository<SearchHashtag, Long>{
    
    Optional<SearchHashtag> findByHashtagName(String name);

}
