package cloneproject.Instagram.domain.search.repository.querydsl;

import java.util.List;
import java.util.Map;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.search.dto.RecommendMemberDto;
import cloneproject.Instagram.domain.search.dto.SearchHashtagDto;
import cloneproject.Instagram.domain.search.dto.SearchMemberDto;
import cloneproject.Instagram.domain.search.entity.Search;

public interface SearchRepositoryQuerydsl {

	List<Search> findHashtagsByTextLike(String text);

	List<Search> findAllByTextLike(String text);

	List<Long> findMemberIdsByTextLike(String text);

	List<Long> findHashtagIdsByTextLike(String text);

	List<RecommendMemberDto> findRecommendMemberDtosOrderByPostCounts();

	void checkMatchingMember(String text, List<Search> searches, List<Long> searchIds);

	void checkMatchingHashtag(String text, List<Search> searches, List<Long> searchIds);

	void checkMatchingMember(String text, List<Long> memberIds);

	void checkMatchingHashtag(String text, List<Long> hashtagIds);

	Map<Long, SearchHashtagDto> findAllSearchHashtagDtoByIdIn(List<Long> searchIds);

	Map<Long, SearchMemberDto> findAllSearchMemberDtoByIdIn(Long loginId, List<Long> ids);

	Map<Long, MemberDto> findAllMemberDtoByIdIn(List<Long> memberIds);

}
