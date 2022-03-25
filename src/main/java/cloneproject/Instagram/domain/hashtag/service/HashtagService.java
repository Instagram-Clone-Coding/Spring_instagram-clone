package cloneproject.Instagram.domain.hashtag.service;

import cloneproject.Instagram.domain.feed.dto.PostDTO;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.follow.exception.CantFollowHashtagException;
import cloneproject.Instagram.domain.follow.exception.CantUnfollowHashtagException;
import cloneproject.Instagram.domain.follow.repository.HashtagFollowRepository;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.exception.HashtagNotFoundException;
import cloneproject.Instagram.domain.hashtag.repository.HashtagRepository;
import cloneproject.Instagram.domain.follow.entity.HashtagFollow;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagFollowRepository hashtagFollowRepository;

    public Page<PostDTO> getHashTagPosts(int page, int size, String name) {
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        System.out.println(name);
        final Optional<Hashtag> findHashtag = hashtagRepository.findByName(name);
        if (findHashtag.isEmpty())
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        return postRepository.findPostDtoPageByHashtag(pageable, member, findHashtag.get());
    }

    @Transactional
    public void followHashtag(String hashtagName){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Hashtag hashtag = hashtagRepository.findByName(hashtagName)
            .orElseThrow(HashtagNotFoundException::new);
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(MemberDoesNotExistException::new);
        
        if(hashtagFollowRepository.existsByMemberIdAndHashtagId(member.getId(), hashtag.getId())){
            throw new CantFollowHashtagException();
        }
        
        HashtagFollow hashtagFollow = HashtagFollow.builder()
            .member(member)
            .hashtag(hashtag)
            .build();
        
            hashtagFollowRepository.save(hashtagFollow);
    }

    @Transactional
    public void unfollowHashtag(String hashtagName){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Hashtag hashtag = hashtagRepository.findByName(hashtagName)
            .orElseThrow(HashtagNotFoundException::new);
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(MemberDoesNotExistException::new);
        
        HashtagFollow hashtagFollow = hashtagFollowRepository.findByMemberIdAndHashtagId(member.getId(), hashtag.getId())
            .orElseThrow(CantUnfollowHashtagException::new);

        hashtagFollowRepository.delete(hashtagFollow);
    }
    
}
