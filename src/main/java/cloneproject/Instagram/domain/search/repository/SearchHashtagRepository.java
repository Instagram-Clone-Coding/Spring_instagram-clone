package cloneproject.Instagram.domain.search.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.search.entity.SearchHashtag;

public interface SearchHashtagRepository extends JpaRepository<SearchHashtag, Long> {

	Optional<SearchHashtag> findByHashtagName(String name);

	List<SearchHashtag> findAllByHashtagIn(List<Hashtag> hashtags);

}
