package cloneproject.Instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import cloneproject.Instagram.dto.member.MiniProfileResponse;
import cloneproject.Instagram.dto.member.SearchedMemberDTO;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;

@DataJpaTest
@DisplayName("Member Repository")
@TestPropertySource("classpath:application-test.yml")
public class MemberRepositoryTest {
    

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FollowRepository followRepository;

    private Member savedMemberOne;
    private Member savedMemberTwo;
    private Member savedMemberThree;
    private static Long loginedId;
    
    @BeforeEach
    void prepare(){
        memberRepository.deleteAll();
        final Member givenMemberOne =  Member.builder()
                                        .username("dlwlrma")
                                        .name("이지금")
                                        .password("1234")
                                        .email("123@gmail.com")
                                        .build();
        final Member givenMemberTwo =  Member.builder()
                                        .username("dlwlrma1")
                                        .name("이지금")
                                        .password("1234")
                                        .email("123@gmail.com")
                                        .build();
        final Member givenMemberThree =  Member.builder()
                                        .username("dlwldms")
                                        .name("이지은")
                                        .password("1234")
                                        .email("123@gmail.com")
                                        .build();
        savedMemberOne = memberRepository.save(givenMemberOne);
        savedMemberTwo = memberRepository.save(givenMemberTwo);
        savedMemberThree = memberRepository.save(givenMemberThree);

        Follow follow = Follow.builder()
                                .member(savedMemberOne)
                                .followMember(savedMemberTwo)
                                .build();
        followRepository.save(follow);
        follow = Follow.builder()
                                .member(savedMemberThree)
                                .followMember(savedMemberOne)
                                .build();
        followRepository.save(follow);
        follow = Follow.builder()
                                .member(savedMemberOne)
                                .followMember(savedMemberThree)
                                .build();
        followRepository.save(follow);
        follow = Follow.builder()
                                .member(savedMemberThree)
                                .followMember(savedMemberTwo)
                                .build();
        followRepository.save(follow);
        loginedId = savedMemberOne.getId();

    }

    @Nested
    @DisplayName("getUserProfile 메서드는")
    class Describe_getUserProfile{
        @Nested
        @DisplayName("로그인한 상태로 요청하면")
        class Context_logined{
            @Test
            @DisplayName("정상적으로 결과를 반환한다")
            void it_returns_well(){
                UserProfileResponse userProfileResponse = memberRepository.getUserProfile(loginedId, savedMemberTwo.getUsername());
                assertEquals(savedMemberTwo.getUsername(), userProfileResponse.getMemberUsername());
                assertEquals(savedMemberTwo.getName(), userProfileResponse.getMemberName());
                assertEquals(2L, userProfileResponse.getMemberFollowersCount());
                assertEquals(savedMemberThree.getUsername(), userProfileResponse.getFollowingMemberFollow());
                assertTrue(userProfileResponse.isFollowing());
            }
        }
        @Nested
        @DisplayName("로그인하지 않은 상태로 요청하면")
        class Context_unlogined{
            @Test
            @DisplayName("정상적으로 결과를 반환하나, 특정 결과가 고정된다")
            void it_returns_well(){
                UserProfileResponse userProfileResponse = memberRepository.getUserProfile(-1L, savedMemberTwo.getUsername());
                assertEquals(savedMemberTwo.getUsername(), userProfileResponse.getMemberUsername());
                assertEquals(savedMemberTwo.getName(), userProfileResponse.getMemberName());
                assertEquals(2L, userProfileResponse.getMemberFollowersCount());
                assertNull(userProfileResponse.getFollowingMemberFollow());
                assertFalse(userProfileResponse.isFollowing());
            }
        }
    }
    @Nested
    @DisplayName("getMiniProfile 메서드는")
    class Describe_getMiniProfile{
        @Nested
        @DisplayName("로그인한 상태로 요청하면")
        class Context_logined{
            @Test
            @DisplayName("정상적으로 결과를 반환한다")
            void it_returns_well(){
                MiniProfileResponse miniProfileResponse = memberRepository.getMiniProfile(loginedId, savedMemberTwo.getUsername());
                assertEquals(savedMemberTwo.getUsername(), miniProfileResponse.getMemberUsername());
                assertEquals(savedMemberTwo.getName(), miniProfileResponse.getMemberName());
                assertEquals(2L, miniProfileResponse.getMemberFollowersCount());
                assertEquals(savedMemberThree.getUsername(), miniProfileResponse.getFollowingMemberFollow());
                assertTrue(miniProfileResponse.isFollowing());
            }
        }
    }

    @Nested
    @DisplayName("searchMember 메서드는")
    class Describe_searchMember{
        @Nested
        @DisplayName("dlwl를 검색하면")
        class Context_dlwl{
            @Test
            @DisplayName("3명의 유저가 검색된다")
            void it_returns_three_users(){
                List<SearchedMemberDTO> searchedMemberDTOs = memberRepository.searchMember(loginedId, "dlwl");
                assertEquals(3, searchedMemberDTOs.size());
                assertEquals(savedMemberOne.getUsername(), searchedMemberDTOs.get(0).getUsername());
                assertEquals(savedMemberThree.getUsername(), searchedMemberDTOs.get(1).getFollowingMemberFollow().get(0).getMemberUsername());
            }
        }
        @Nested
        @DisplayName("dlwlrma를 검색하면")
        class Context_dlwlrma{
            @Test
            @DisplayName("2명의 유저가 검색된다")
            void it_returns_two_users(){
                List<SearchedMemberDTO> searchedMemberDTOs = memberRepository.searchMember(loginedId, "dlwlrma");
                assertEquals(2, searchedMemberDTOs.size());
                assertEquals(savedMemberOne.getUsername(), searchedMemberDTOs.get(0).getUsername());
                assertEquals(savedMemberThree.getUsername(), searchedMemberDTOs.get(1).getFollowingMemberFollow().get(0).getMemberUsername());
            }
        }
    }
    @Nested
    @DisplayName("findAllByUsernames 메서드는")
    class Describe_findAllByUsernames{
        @Nested
        @DisplayName("2개의 username을 넘기면")
        class Context_two_usernames{
            @Test
            @DisplayName("2명의 유저가 리턴된다")
            void it_returns_two_users(){
                List<String> usernameList = new ArrayList<>();
                usernameList.add(savedMemberOne.getUsername());
                usernameList.add(savedMemberTwo.getUsername());
                List<Member> members = memberRepository.findAllByUsernames(usernameList);
                assertEquals(2, members.size());
            }
        }
    }

    
}
