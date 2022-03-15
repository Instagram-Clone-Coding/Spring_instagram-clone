package cloneproject.Instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import cloneproject.Instagram.dto.member.FollowerDTO;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;

@DataJpaTest
@DisplayName("Follow Repository")
@TestPropertySource("classpath:application-test.yml")
public class FollowRepositoryTest {
    
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private MemberRepository memberRepository;

    
    private Member savedMemberOne;
    private Member savedMemberTwo;
    private Member savedMemberThree;
    private Long loginedId;
    
    @BeforeEach
    void prepare(){
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
    @DisplayName("getFollowings 메서드는")
    class Describe_getFollowings{
        @Nested
        @DisplayName("정상적인 인자로 호출하면")
        class Context_correct_parameters{
            @Test
            @DisplayName("팔로잉들을 반환한다")
            void it_returns_followings(){
                List<FollowerDTO> followings = followRepository.getFollowings(loginedId, savedMemberOne.getId());
                assertEquals(2, followings.size());
                assertEquals(savedMemberTwo.getUsername(), followings.get(0).getMember().getUsername());
                assertEquals(savedMemberThree.getUsername(), followings.get(1).getMember().getUsername());
                assertTrue(followings.get(1).isFollower());
            }   
        }
    }

    @Nested
    @DisplayName("getFollowers 메서드는")
    class Describe_getFollowers{
        @Nested
        @DisplayName("정상적인 인자로 호출하면")
        class Context_correct_parameters{
            @Test
            @DisplayName("팔로워들을 반환한다")
            void it_returns_followers(){
                List<FollowerDTO> followers = followRepository.getFollowers(loginedId, savedMemberTwo.getId());
                assertEquals(2, followers.size());
                assertEquals(savedMemberOne.getUsername(), followers.get(0).getMember().getUsername());
                assertEquals(savedMemberThree.getUsername(), followers.get(1).getMember().getUsername());
                assertTrue(followers.get(0).isMe());
                assertTrue(followers.get(1).isFollower());
            }   
        }
    }
}
