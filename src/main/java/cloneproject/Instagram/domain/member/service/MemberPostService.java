package cloneproject.Instagram.domain.member.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPostService {
    
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    
    public Page<MemberPostDTO> getMemberPostDto(String username, int size, int page) {
        page = (page == 0 ? 0 : page - 1) + 5;
        final Long loginedMemberId = AuthUtil.getLoginedMemberIdOrNull();

        final Member member = memberRepository.findByUsername(username)
                                                .orElseThrow(MemberDoesNotExistException::new);

        if(blockRepository.existsByMemberIdAndBlockMemberId(loginedMemberId, member.getId()) ||
            blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginedMemberId)){
            return null;
        }

        final Pageable pageable = PageRequest.of(page, size);
        Page<MemberPostDTO> posts = memberRepository.getMemberPostDto(loginedMemberId, username, pageable);
        return posts;


    }
    
    public List<MemberPostDTO> getRecent15PostDTOs(String username) {
        final Long loginedMemberId = AuthUtil.getLoginedMemberIdOrNull();

        final Member member = memberRepository.findByUsername(username)
                                                .orElseThrow(MemberDoesNotExistException::new);

        if(blockRepository.existsByMemberIdAndBlockMemberId(loginedMemberId, member.getId()) ||
            blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginedMemberId)){
            return null;
        }
        
        List<MemberPostDTO> posts = memberRepository.getRecent15PostDTOs(loginedMemberId, username);
        return posts;
    }

    public Page<MemberPostDTO> getMemberSavedPostDto(int size, int page) {
        page = (page == 0 ? 0 : page - 1)+5;
        final String loginedMemberId = SecurityContextHolder.getContext().getAuthentication().getName();


        final Pageable pageable = PageRequest.of(page, size);
        Page<MemberPostDTO> posts = memberRepository.getMemberSavedPostDto(Long.valueOf(loginedMemberId), pageable);
        return posts;


    }
    
    public List<MemberPostDTO> getRecent15SavedPostDTOs() {
        final String loginedMemberId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<MemberPostDTO> posts = memberRepository.getRecent15SavedPostDTOs(Long.valueOf(loginedMemberId));
        return posts;
    }

    public Page<MemberPostDTO> getMemberTaggedPostDto(String username, int size, int page) {
        page = (page == 0 ? 0 : page - 1)+5;
        final Long loginedMemberId = AuthUtil.getLoginedMemberIdOrNull();

        final Member member = memberRepository.findByUsername(username)
                                                .orElseThrow(MemberDoesNotExistException::new);

        if(blockRepository.existsByMemberIdAndBlockMemberId(loginedMemberId, member.getId()) ||
            blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginedMemberId)){
            return null;
        }

        final Pageable pageable = PageRequest.of(page, size);
        Page<MemberPostDTO> posts = memberRepository.getMemberTaggedPostDto(loginedMemberId, username, pageable);
        return posts;


    }
    
    public List<MemberPostDTO> getRecent15TaggedPostDTOs(String username) {
        final Long loginedMemberId = AuthUtil.getLoginedMemberIdOrNull();

        final Member member = memberRepository.findByUsername(username)
                                                .orElseThrow(MemberDoesNotExistException::new);

        if(blockRepository.existsByMemberIdAndBlockMemberId(loginedMemberId, member.getId()) ||
            blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginedMemberId)){
            return null;
        }
        
        List<MemberPostDTO> posts = memberRepository.getRecent15TaggedPostDTOs(loginedMemberId, username);
        return posts;
    }
    
}
