package cloneproject.Instagram.domain.search.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.exception.HashtagNotFoundException;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.search.dto.SearchDto;
import cloneproject.Instagram.domain.search.dto.SearchHashtagDto;
import cloneproject.Instagram.domain.search.dto.SearchMemberDto;
import cloneproject.Instagram.domain.search.entity.RecentSearch;
import cloneproject.Instagram.domain.search.entity.Search;
import cloneproject.Instagram.domain.search.entity.SearchHashtag;
import cloneproject.Instagram.domain.search.repository.RecentSearchRepository;
import cloneproject.Instagram.domain.search.repository.SearchHashtagRepository;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
import cloneproject.Instagram.domain.search.repository.SearchRepository;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.global.error.exception.EntityTypeInvalidException;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

	private final AuthUtil authUtil;
	private final SearchRepository searchRepository;
	private final SearchMemberRepository searchMemberRepository;
	private final SearchHashtagRepository searchHashtagRepository;
	private final RecentSearchRepository recentSearchRepository;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final FollowRepository followRepository;

	private static final int FIRST_PAGE_SIZE = 15;
	private static final int PAGE_SIZE = 5;
	private static final int PAGE_OFFSET = 2;
	private static final int MAX_FOLLOWING_MEMBER_FOLLOW_COUNT = 3;

	public List<SearchDto> searchByText(String text) {
		text = text.trim();
		final Long loginId = authUtil.getLoginMemberId();
		List<Search> searches;
		if (text.charAt(0) == '#') {
			searches = searchRepository.findHashtagsByTextLike(text.substring(1));
		} else {
			searches = searchRepository.findAllByTextLike(text);
		}

		final List<Long> searchIds = searches.stream()
			.map(Search::getId)
			.collect(Collectors.toList());

		searchRepository.checkMatchingHashtag(text, searches, searchIds);
		searchRepository.checkMatchingMember(loginId, text, searches, searchIds);

		return setSearchContent(loginId, searches, searchIds);
	}

	public Page<SearchDto> getTop15RecentSearches() {
		final Long loginId = authUtil.getLoginMemberId();
		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final List<Search> searches = recentSearchRepository.findAllByMemberId(loginId, pageable);

		final List<Long> searchIds = searches.stream()
			.map(Search::getId)
			.collect(Collectors.toList());

		final List<SearchDto> searchDtos = setSearchContent(loginId, searches, searchIds);
		final Long total = recentSearchRepository.getRecentSearchCount(loginId);
		return new PageImpl<>(searchDtos, pageable, total);
	}

	public Page<SearchDto> getRecentSearches(int page) {
		final Long loginId = authUtil.getLoginMemberId();
		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, PAGE_SIZE);
		final List<Search> searches = recentSearchRepository.findAllByMemberId(loginId, pageable);

		final List<Long> searchIds = searches.stream()
			.map(Search::getId)
			.collect(Collectors.toList());

		final List<SearchDto> searchDtos = setSearchContent(loginId, searches, searchIds);
		final Long total = recentSearchRepository.getRecentSearchCount(loginId);
		return new PageImpl<>(searchDtos, pageable, total);
	}

	@Transactional
	public void deleteRecentSearch(String entityName, String entityType) {
		final Long loginId = authUtil.getLoginMemberId();
		switch (entityType) {
			case "MEMBER":
				recentSearchRepository.findRecentSearchByUsername(loginId, entityName)
					.ifPresent(recentSearchRepository::delete);
				break;
			case "HASHTAG":
				recentSearchRepository.findRecentSearchByHashtagName(loginId, entityName)
					.ifPresent(recentSearchRepository::delete);
				break;
			default:
				throw new EntityTypeInvalidException();
		}
	}

	@Transactional
	public void deleteAllRecentSearch() {
		final Long loginId = authUtil.getLoginMemberId();
		recentSearchRepository.deleteAllByMemberId(loginId);
	}

	@Transactional
	public void markSearchedEntity(String entityName, String entityType) {
		final Member loginMember = authUtil.getLoginMember();
		final Search search;
		switch (entityType) {
			case "MEMBER":
				search = searchMemberRepository.findByMemberUsername(entityName)
					.orElseThrow(MemberDoesNotExistException::new);
				break;
			case "HASHTAG":
				search = searchHashtagRepository.findByHashtagName(entityName)
					.orElseThrow(HashtagNotFoundException::new);
				break;
			default:
				throw new EntityTypeInvalidException();
		}
		search.upCount();
		searchRepository.save(search);

		final RecentSearch recentSearch = recentSearchRepository.findByMemberIdAndSearchId(loginMember.getId(),
				search.getId())
			.orElse(RecentSearch.builder()
				.member(loginMember)
				.search(search)
				.build());
		recentSearch.updateLastSearchedDate();
		recentSearchRepository.save(recentSearch);
	}

	private List<SearchDto> setSearchContent(Long loginId, List<Search> searches, List<Long> searchIds) {
		final Map<Long, SearchMemberDto> memberMap = searchRepository.findAllSearchMemberDtoByIdIn(loginId, searchIds);
		final Map<Long, SearchHashtagDto> hashtagMap = searchRepository.findAllSearchHashtagDtoByIdIn(searchIds);
		final List<String> searchUsernames = memberMap.values().stream().map(s -> s.getMember().getUsername())
			.collect(Collectors.toList());

		// 스토리 주입
		memberMap.forEach((id, member) -> {
			member.getMember().setHasStory(memberStoryRedisRepository.findById(id).isPresent());
		});

		// 팔로우 주입
		final Map<String, List<FollowDto>> followsMap = followRepository.getFollowingMemberFollowMap(loginId,
			searchUsernames);
		memberMap.forEach(
			(id, member) -> member.setFollowingMemberFollow(
				followsMap.get(
						member.getMember().getUsername()
					), MAX_FOLLOWING_MEMBER_FOLLOW_COUNT)
		);

		return searches.stream()
			.map(search -> {
				switch (search.getDtype()) {
					case "MEMBER":
						return memberMap.get(search.getId());
					case "HASHTAG":
						return hashtagMap.get(search.getId());
					default:
						return null;
				}
			})
			.collect(Collectors.toList());
	}

	@Transactional
	public void createSearchHashtag(Hashtag hashtag) {
		searchHashtagRepository.save(new SearchHashtag(hashtag));
	}

	@Transactional
	public void deleteSearchHashtags(List<Hashtag> hashtags) {
		final List<SearchHashtag> searchHashtags = searchHashtagRepository.findAllByHashtagIn(hashtags);
		searchHashtagRepository.deleteAllInBatch(searchHashtags);
	}

}
