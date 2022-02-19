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
import cloneproject.Instagram.entity.member.Block;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.AlreadyBlockException;
import cloneproject.Instagram.exception.CantBlockMyselfException;
import cloneproject.Instagram.exception.CantUnblockException;
import cloneproject.Instagram.exception.CantUnblockMyselfException;
import cloneproject.Instagram.repository.BlockRepository;
import cloneproject.Instagram.repository.FollowRepository;
import cloneproject.Instagram.repository.MemberRepository;

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
                            .name("이지금")
                            .email("aaa@gmail.com")
                            .password("1234")
                            .build();
        memberTwo = Member.builder()
                            .username("dlwlrma1")
                            .name("이지금1")
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
    @DisplayName("block 메서드는")
    class Describe_block{
        @Nested
        @DisplayName("차단 한 적이 없다면")
        class Context_did_not_block{
            @Test
            @WithMockCustomUser
            @DisplayName("팔로우 된다")
            void it_follows_well(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertTrue(blockService.block("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("자기자신을 차단 하려하면")
        class Context_block_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("에러가 발생한다")
            void it_occurs_exeption(){
                assertThrows(CantBlockMyselfException.class,()->blockService.block("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("차단 한 적이 있다면")
        class Context_already_block{
            @Test
            @WithMockCustomUser
            @DisplayName("이미 차단 했다는 예외가 발생한다.")
            void it_occurs_exception(){
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(AlreadyBlockException.class,()->blockService.block("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("맞팔로우 상태였다면")
        class Context_following_and_follower{
            @Test
            @WithMockCustomUser
            @DisplayName("팔로우가 해제되고 차단된다")
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
    @DisplayName("unblock 메서드는")
    class Describe_unblock{
        @Nested
        @DisplayName("차단 한 적이 없다면")
        class Context_did_not_block{
            @Test
            @WithMockCustomUser
            @DisplayName("차단해제 할수 없음 예외가 발생한다")
            void it_occurs_exception(){
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(CantUnblockException.class,()->blockService.unblock("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("자기자신을 차단해제하려 한다면")
        class Context_unfollow_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("에러가 발생한다.")
            void it_occurs_exception(){
                assertThrows(CantUnblockMyselfException.class,()->blockService.unblock("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("차단 한 적이 있다면")
        class Context_already_blocking{
            @Test
            @WithMockCustomUser
            @DisplayName("정상적으로 차단해제 된다.")
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
