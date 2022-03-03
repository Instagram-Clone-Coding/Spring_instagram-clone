package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.hashtag.HashtagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HashTagRepositoryQuerydsl {

    Page<HashtagDTO> findHashtagDtoPageLikeName(Pageable pageable, String name);
}
