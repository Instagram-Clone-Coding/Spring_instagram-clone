package cloneproject.Instagram.domain.search.repository.querydsl;

import java.util.List;
import java.util.Map;

import cloneproject.Instagram.domain.search.dto.SearchHashtagDTO;
import cloneproject.Instagram.domain.search.dto.SearchMemberDTO;
import cloneproject.Instagram.domain.search.entity.Search;

public interface SearchRepositoryQuerydsl {

	List<Search> findHashtagsByTextLike(String text);

	List<Search> findAllByTextLike(String text);

	void checkMatchingMember(Long loginId, String text, List<Search> searches, List<Long> searchIds);

	void checkMatchingHashtag(String text, List<Search> searches, List<Long> searchIds);

	Map<Long, SearchHashtagDTO> findAllSearchHashtagDTOsByIdIn(List<Long> searchIds);

	Map<Long, SearchMemberDTO> findAllSearchMemberDTOsByIdIn(Long loginId, List<Long> ids);

}
