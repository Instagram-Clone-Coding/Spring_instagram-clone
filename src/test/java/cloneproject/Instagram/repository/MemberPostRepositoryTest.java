// package cloneproject.Instagram.repository;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.TestPropertySource;

// import cloneproject.Instagram.entity.member.Member;
// import cloneproject.Instagram.entity.post.Post;
// import cloneproject.Instagram.repository.post.PostRepository;
// TODO 보류
// @DataJpaTest
// @DisplayName("Member Post Repository")
// @TestPropertySource("classpath:application-test.yml")
// public class MemberPostRepositoryTest {
    
//     @Autowired
//     private MemberRepository memberRepository;

//     @Autowired
//     private PostRepository postRepository;
    
//     private Member savedMemberOne;
//     private Member savedMemberTwo;
//     private Post savedPostOne;
//     private Post savedPostTwo;
//     private static Long loginedId;
    
//     @BeforeEach
//     void prepare(){
//         memberRepository.deleteAll();
//         final Member givenMemberOne =  Member.builder()
//                                         .username("dlwlrma")
//                                         .name("이지금")
//                                         .password("1234")
//                                         .email("123@gmail.com")
//                                         .build();
//         final Member givenMemberTwo =  Member.builder()
//                                         .username("dlwlrma1")
//                                         .name("이지금")
//                                         .password("1234")
//                                         .email("123@gmail.com")
//                                         .build();
//         savedMemberOne = memberRepository.save(givenMemberOne);
//         savedMemberTwo = memberRepository.save(givenMemberTwo);
//         Post postOne = Post.builder()
//                 .content("test content1")
//                 .member(givenMemberOne)
//                 .build();
//         Post postTwo = Post.builder()
//                 .content("test content2")
//                 .member(givenMemberOne)
//                 .build();
//         savedPostOne = postRepository.save(postOne);
//         savedPostTwo = postRepository.save(postTwo);
//         loginedId = savedMemberOne.getId();
//     }

// }
