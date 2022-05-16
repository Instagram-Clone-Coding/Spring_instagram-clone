package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.repository.querydsl.PostLikeRepositoryQuerydsl;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryQuerydsl {

	List<PostLike> findAllByPost(Post post);

	Optional<PostLike> findByMemberAndPost(Member member, Post post);

}
