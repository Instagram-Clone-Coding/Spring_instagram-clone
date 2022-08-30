package cloneproject.Instagram.domain.hashtag.repository.querydsl;

import cloneproject.Instagram.domain.hashtag.dto.HashtagProfileResponse;
import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;

public interface HashtagPostRepositoryQuerydsl {

	HashtagProfileResponse findHashtagProfileByLoginMemberIdAndHashtagId(Long loginMemberId, Long hashtagId);

}
