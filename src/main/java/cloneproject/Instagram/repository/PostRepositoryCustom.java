package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepositoryCustom {
    Slice<PostDTO> findPostDtoPage(Member member, Pageable pageable);
}
