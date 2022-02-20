package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Bookmark;
import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberAndPost(Member member, Post post);
}
