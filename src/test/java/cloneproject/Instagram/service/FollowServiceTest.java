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
import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.exception.AlreadyFollowException;
import cloneproject.Instagram.domain.follow.exception.CantFollowMyselfException;
import cloneproject.Instagram.domain.follow.exception.CantUnfollowException;
import cloneproject.Instagram.domain.follow.exception.CantUnfollowMyselfException;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.follow.service.FollowService;
import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;

@ExtendWith(SpringExtension.class)
@DisplayName("Follow Service")
public class FollowServiceTest {
    
    @InjectMocks
    private FollowService followService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private AlarmService alarmService;

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
    @DisplayName("follow 메서드는")
    class Describe_follow{
        @Nested
        @DisplayName("팔로우 한 적이 없다면")
        class Context_did_not_follow{
            @Test
            @WithMockCustomUser
            @DisplayName("팔로우 된다")
            void it_follows_well(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertTrue(followService.follow("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("자기자신을 팔로우하려 한다면")
        class Context_follow_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("에러가 발생한다.")
            void it_occurs_exception(){
                assertThrows(CantFollowMyselfException.class,()->followService.follow("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("팔로우 한 적이 있다면")
        class Context_already_following{
            @Test
            @WithMockCustomUser
            @DisplayName("이미 팔로우 했다는 예외가 발생한다.")
            void it_occurs_exception(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(AlreadyFollowException.class,()->followService.follow("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("차단 당했다면")
        class Context_blocked{
            @Test
            @WithMockCustomUser
            @DisplayName("찾을 수 없는 유저 예외가 발생한다")
            void it_occurs_exception(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(true);
                assertThrows(MemberDoesNotExistException.class,()->followService.follow("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("차단 했다면")
        class Context_blocking{
            @Test
            @WithMockCustomUser
            @DisplayName("차단이 해제된 다음 팔로우된다")
            void it_occurs_exception(){
                Block block = Block.builder().member(memberOne).blockMember(memberTwo).build();
                when(blockRepository.findByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(Optional.of(block));

                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(true);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);

                assertTrue(followService.follow("dlwlrma1"));
                verify(blockRepository, times(1)).delete(block);
            }
        }
    }

    @Nested
    @DisplayName("unfollow 메서드는")
    class Describe_unfollow{
        @Nested
        @DisplayName("팔로우 한 적이 없다면")
        class Context_did_not_follow{
            @Test
            @WithMockCustomUser
            @DisplayName("언팔로우 할수 없음 예외가 발생한다")
            void it_occurs_exception(){
                when(followRepository.existsByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertThrows(CantUnfollowException.class,()->followService.unfollow("dlwlrma1"));
            }
        }
        @Nested
        @DisplayName("자기자신을 언팔로우하려 한다면")
        class Context_unfollow_myself{
            @Test
            @WithMockCustomUser
            @DisplayName("에러가 발생한다.")
            void it_occurs_exception(){
                assertThrows(CantUnfollowMyselfException.class,()->followService.unfollow("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("팔로우 한 적이 있다면")
        class Context_already_following{
            @Test
            @WithMockCustomUser
            @DisplayName("정상적으로 언팔로우 된다.")
            void it_unfollows_well(){
                Follow follow = Follow.builder().member(memberOne).followMember(memberTwo).build();
                when(followRepository.findByMemberIdAndFollowMemberId(1L, 2L)).thenReturn(Optional.of(follow));
                when(blockRepository.existsByMemberIdAndBlockMemberId(1L, 2L)).thenReturn(false);
                when(blockRepository.existsByMemberIdAndBlockMemberId(2L, 1L)).thenReturn(false);
                assertTrue(followService.unfollow("dlwlrma1"));
            }
        }
    }
}
