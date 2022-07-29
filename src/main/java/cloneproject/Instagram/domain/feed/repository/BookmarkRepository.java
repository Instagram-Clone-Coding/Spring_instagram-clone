package cloneproject.Instagram.domain.feed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.feed.entity.Bookmark;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByMemberAndPost(Member member, Post post);

}
