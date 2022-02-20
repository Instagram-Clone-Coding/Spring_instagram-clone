package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.entity.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryQuerydsl {

    List<PostLike> findAllByPost(Post post);

    Optional<PostLike> findByMemberAndPost(Member member, Post post);
}
