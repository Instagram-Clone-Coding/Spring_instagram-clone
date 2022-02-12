package cloneproject.Instagram.service;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.entity.member.Block;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.AlreadyBlockException;
import cloneproject.Instagram.exception.CantBlockMyselfException;
import cloneproject.Instagram.exception.CantUnblockException;
import cloneproject.Instagram.exception.CantUnblockMyselfException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.BlockRepository;
import cloneproject.Instagram.repository.FollowRepository;
import cloneproject.Instagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public boolean block(String blockMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId))
                                        .orElseThrow(MemberDoesNotExistException::new);
        final Member blockMember = memberRepository.findByUsername(blockMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        if(member.getId().equals(blockMember.getId())){
            throw new CantBlockMyselfException();
        }
        if(blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), blockMember.getId())){
            throw new AlreadyBlockException();
        }

        // 팔로우가 되어있었다면 언팔로우
        Optional<Follow> follow = followRepository.findByMemberIdAndFollowMemberId(member.getId(), blockMember.getId());
        if(follow.isPresent()){
            followRepository.delete(follow.get());
        }
        follow = followRepository.findByMemberIdAndFollowMemberId(blockMember.getId(), member.getId());
        if(follow.isPresent()){
            followRepository.delete(follow.get());
        }

        Block block = new Block(member, blockMember);
        blockRepository.save(block);
        return true;
    }

    @Transactional
    public boolean unblock(String blockMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member blockMember = memberRepository.findByUsername(blockMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        if(Long.valueOf(memberId).equals(blockMember.getId())){
            throw new CantUnblockMyselfException();
        }
        Block block = blockRepository.findByMemberIdAndBlockMemberId(Long.valueOf(memberId), blockMember.getId())
                                        .orElseThrow(CantUnblockException::new);
        blockRepository.delete(block);
        return true;
    }

}