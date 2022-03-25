package cloneproject.Instagram.domain.feed.repository.querydsl;

import cloneproject.Instagram.domain.feed.dto.PostDTO;
import cloneproject.Instagram.domain.feed.dto.PostResponse;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryQuerydsl {

    Page<PostDTO> findPostDtoPage(Member member, Pageable pageable);

    List<PostDTO> findRecent10PostDTOs(Long memberId);

    Optional<PostResponse> findPostResponse(Long postId, Long memberId);

    Page<PostDTO> findPostDtoPageByHashtag(Pageable pageable, Member member, Hashtag hashtag);
}
