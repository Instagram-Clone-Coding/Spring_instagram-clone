package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostDTO> findPostDtoPage(Member member, Pageable pageable);
}
