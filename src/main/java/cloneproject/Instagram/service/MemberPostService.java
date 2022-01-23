package cloneproject.Instagram.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.BlockRepository;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.util.AuthUtil;
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

}
