package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.post.PostResponse;
import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.member.Member;
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
