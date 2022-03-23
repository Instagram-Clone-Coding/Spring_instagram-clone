package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    List<Hashtag> findAllByNameIn(Set<String> names);

    Optional<Hashtag> findByName(String name);
}
