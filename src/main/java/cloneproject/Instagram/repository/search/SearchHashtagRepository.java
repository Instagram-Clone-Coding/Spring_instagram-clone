package cloneproject.Instagram.repository.search;

import java.util.List;
import java.util.Optional;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.search.SearchHashtag;

public interface SearchHashtagRepository extends JpaRepository<SearchHashtag, Long> {

    Optional<SearchHashtag> findByHashtagName(String name);

    List<SearchHashtag> findAllByHashtagIn(List<Hashtag> hashtags);
}
