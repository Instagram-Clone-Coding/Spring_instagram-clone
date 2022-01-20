package cloneproject.Instagram.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.post.PostRepository;
import cloneproject.Instagram.util.PostUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPostService {
    
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    
    public Page<MemberPostDTO> getPostDtoPage(String username, int size, int page) {
        page = (page == 0 ? 0 : page - 1) + 5;
        final Pageable pageable = PageRequest.of(page, size);
        final Member member = memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);
        Page<Post> posts = postRepository.findByMemberId(member.getId(), pageable);
        return posts.map(PostUtil::convertPostToMemberPostDTO);


    }
    
    public Page<MemberPostDTO> getRecent15PostDTOs(String username) {
        final Pageable pageable = PageRequest.of(0, 15);
        final Member member = memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);
        Page<Post> posts = postRepository.findByMemberId(member.getId(), pageable);
        return posts.map(PostUtil::convertPostToMemberPostDTO);
    }

}
