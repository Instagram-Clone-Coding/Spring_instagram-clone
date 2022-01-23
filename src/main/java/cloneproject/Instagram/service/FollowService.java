package cloneproject.Instagram.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.dto.member.FollowerDTO;
import cloneproject.Instagram.entity.member.Block;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.AlreadyFollowException;
import cloneproject.Instagram.exception.CantFollowMyselfException;
import cloneproject.Instagram.exception.CantUnfollowException;
import cloneproject.Instagram.exception.CantUnfollowMyselfException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.BlockRepository;
import cloneproject.Instagram.repository.FollowRepository;
import cloneproject.Instagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public boolean follow(String followMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId))
                                        .orElseThrow(MemberDoesNotExistException::new);
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);

        if(member.getId().equals(followMember.getId())){
            throw new CantFollowMyselfException();
        }
        if(followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId())){
            throw new AlreadyFollowException();
        }

        // 차단당했다면
        if(blockRepository.existsByMemberIdAndBlockMemberId(followMember.getId(), member.getId())){
            throw new MemberDoesNotExistException();
        }
        
        // 차단했었다면 차단해제
        Optional<Block> blocking = blockRepository.findByMemberIdAndBlockMemberId(member.getId(), followMember.getId());
        if(blocking.isPresent()){
            blockRepository.delete(blocking.get());
        }

        Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        return true;
        
    }

    @Transactional
    public boolean unfollow(String followMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        if(Long.valueOf(memberId).equals(followMember.getId())){
            throw new CantUnfollowMyselfException();
        }
        Follow follow = followRepository.findByMemberIdAndFollowMemberId(Long.valueOf(memberId), followMember.getId())
                                        .orElseThrow(CantUnfollowException::new);
        followRepository.delete(follow);
        return true;
    }

    @Transactional(readOnly = true)
    public List<FollowerDTO> getFollowings(String memberUsername){ 
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        final Member toMember = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);

        final List<FollowerDTO> result = followRepository.getFollwings(Long.valueOf(memberId), toMember.getId());
        return result;
    }

    @Transactional(readOnly = true)
    public List<FollowerDTO> getFollowers(String memberUsername){ 
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        final Member toMember = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);

        final List<FollowerDTO> result = followRepository.getFollwers(Long.valueOf(memberId), toMember.getId());
        return result;
    }
    
}
