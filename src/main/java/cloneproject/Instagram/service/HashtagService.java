package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.hashtag.HashtagDTO;
import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
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

    public Page<HashtagDTO> getHashTagsLikeName(int page, int size, String name) {
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        return hashtagRepository.findHashtagDtoPageLikeName(pageable, name);
    }
}
