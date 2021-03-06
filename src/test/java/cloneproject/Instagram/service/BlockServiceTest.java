package cloneproject.Instagram.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import cloneproject.Instagram.WithMockCustomUser;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.AlreadyBlockException;
import cloneproject.Instagram.domain.member.exception.CantBlockMyselfException;
import cloneproject.Instagram.domain.member.exception.CantUnblockException;
import cloneproject.Instagram.domain.member.exception.CantUnblockMyselfException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.service.BlockService;

@ExtendWith(SpringExtension.class)
@DisplayName("Block Service")
public class BlockServiceTest {
    
    @InjectMocks
    private BlockService blockService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private BlockRepository blockRepository;

    private Member memberOne;
    private Member memberTwo;

    @BeforeEach
    void setRepostiory(){
        memberOne = Member.builder()
                            .username("dlwlrma")
                            .name("?????????")
                            .email("aaa@gmail.com")
                            .password("1234")
                            .build();
        memberTwo = Member.builder()
                            .username("dlwlrma1")
                            .name("?????????1")
                            .email("aaa@gmail.com")
                            .password("1234")
                            .build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberOne));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(memberTwo));
        when(memberRepository.findByUsername("dlwlrma")).thenReturn(Optional.of(memberOne));
        when(memberRepository.findByUsername("dlwlrma1")).thenReturn(Optional.of(memberTwo));
        ReflectionTestUtils.setField(memberOne, "id", 1L);
        ReflectionTestUtils.setField(memberTwo, "id", 2L);
    }
    
    @Nested
    @DisplayName("block ????????????")
    class Describe_block{
        @Nested
        @DisplayName("?????? ??? ?????? ?????????")
        class Context_did_not_block{
            @Test
            @WithMockCustomUser
            @DisplayName("????????? ??????")
            void it_follows_well(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertTrue(blockService.block("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("??????????????? ?????? ????????????")
        class Context_block_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("????????? ????????????")
            void it_occurs_exeption(){
                assertThrows(CantBlockMyselfException.class,()->blockService.block("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("?????? ??? ?????? ?????????")
        class Context_already_block{
            @Test
            @WithMockCustomUser
            @DisplayName("?????? ?????? ????????? ????????? ????????????.")
            void it_occurs_exception(){
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(AlreadyBlockException.class,()->blockService.block("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("???????????? ???????????????")
        class Context_following_and_follower{
            @Test
            @WithMockCustomUser
            @DisplayName("???????????? ???????????? ????????????")
            void it_occurs_exception(){
                Follow followOne = Follow.builder().member(memberOne).followMember(memberTwo).build();
                Follow followTwo = Follow.builder().member(memberTwo).followMember(memberOne).build();
                when(followRepository.findByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(Optional.of(followOne));
                when(followRepository.findByMemberIdAndFollowMemberId(2L, 1L)).thenReturn(Optional.of(followTwo));
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(true);
                when(followRepository.existsByMemberIdAndFollowMemberId(2L, 1L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);

                assertTrue(blockService.block("dlwlrma1"));
                verify(followRepository,times(1)).delete(followOne);
                verify(followRepository,times(1)).delete(followTwo);
            }
        }
    }

    @Nested
    @DisplayName("unblock ????????????")
    class Describe_unblock{
        @Nested
        @DisplayName("?????? ??? ?????? ?????????")
        class Context_did_not_block{
            @Test
            @WithMockCustomUser
            @DisplayName("???????????? ?????? ?????? ????????? ????????????")
            void it_occurs_exception(){
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(CantUnblockException.class,()->blockService.unblock("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("??????????????? ?????????????????? ?????????")
        class Context_unfollow_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("????????? ????????????.")
            void it_occurs_exception(){
                assertThrows(CantUnblockMyselfException.class,()->blockService.unblock("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("?????? ??? ?????? ?????????")
        class Context_already_blocking{
            @Test
            @WithMockCustomUser
            @DisplayName("??????????????? ???????????? ??????.")
            void it_unblocks_well(){
                Block block = Block.builder().member(memberOne).blockMember(memberTwo).build();
                when(blockRepository.findByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(Optional.of(block));
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertTrue(blockService.unblock("dlwlrma1"));
            }
        }
    }
}
