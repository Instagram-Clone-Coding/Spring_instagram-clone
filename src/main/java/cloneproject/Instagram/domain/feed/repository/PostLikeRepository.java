package cloneproject.Instagram.domain.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.repository.querydsl.PostLikeRepositoryQuerydsl;
import cloneproject.Instagram.domain.member.entity.Member;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryQuerydsl {

	List<PostLike> findAllByPost(Post post);

	Optional<PostLike> findByMemberAndPost(Member member, Post post);

	@Query("select pl from PostLike pl join fetch pl.member where pl.post.id = :postId")
	List<PostLike> findAllWithMemberByPostId(@Param("postId") Long postId);

}
