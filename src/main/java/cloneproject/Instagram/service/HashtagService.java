package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.member.HashtagFollow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.CantFollowHashtagException;
import cloneproject.Instagram.exception.CantUnfollowHashtagException;
import cloneproject.Instagram.exception.HashtagNotFoundException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.HashtagFollowRepository;
import cloneproject.Instagram.repository.HashtagRepository;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.post.PostRepository;
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
