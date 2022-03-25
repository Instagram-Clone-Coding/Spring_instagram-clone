package cloneproject.Instagram.domain.search.service;

import java.util.List;
import java.util.stream.Collectors;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.exception.HashtagNotFoundException;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.search.dto.SearchDTO;
import cloneproject.Instagram.domain.search.entity.SearchHashtag;
import cloneproject.Instagram.domain.search.entity.SearchMember;
import cloneproject.Instagram.domain.search.repository.SearchHashtagRepository;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
import cloneproject.Instagram.domain.search.repository.SearchRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final SearchMemberRepository searchMemberRepository;
    private final SearchHashtagRepository searchHashtagRepository;

    @Transactional(readOnly = true)
    public List<SearchDTO> searchByText(String text) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final List<SearchDTO> result;
        if (text.charAt(0) == '#') {
            result = searchRepository.searchByTextOnlyHashtag(Long.valueOf(memberId), text.substring(1));
        } else {
            result = searchRepository.searchByText(Long.valueOf(memberId), text);
        }
        return result;
    }

    @Transactional
    public void increaseSearchCount(String entityName, String entityType) {
        switch(entityType){
            case "MEMBER":
                SearchMember searchMember = searchMemberRepository.findByMemberUsername(entityName)
                        .orElseThrow(MemberDoesNotExistException::new);
                searchMember.upCount();
                searchMemberRepository.save(searchMember);
                break;
            case "HASHTAG":
                SearchHashtag searchHashtag = searchHashtagRepository.findByHashtagName(entityName)
                        .orElseThrow(HashtagNotFoundException::new);
                searchHashtag.upCount();
                searchHashtagRepository.save(searchHashtag);
                break;
        }
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
