package cloneproject.Instagram.domain.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findAllByNameIn(Set<String> names);

    List<Hashtag> findAllByIdIn(Collection<Long> ids);

    Optional<Hashtag> findByName(String name);

}
