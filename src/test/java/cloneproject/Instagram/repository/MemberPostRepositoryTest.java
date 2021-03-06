package cloneproject.Instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import cloneproject.Instagram.domain.feed.entity.Bookmark;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.repository.BookmarkRepository;
import cloneproject.Instagram.domain.feed.repository.CommentRepository;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.repository.PostLikeRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.util.ImageUtil;
import cloneproject.Instagram.global.vo.Image;
@DataJpaTest
@DisplayName("Member Post Repository")
@TestPropertySource("classpath:application-test.yml")
public class MemberPostRepositoryTest {
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private PostImageRepository postImageRepository;
    
    private Member savedMemberOne;
    private Member savedMemberTwo;
    private Post savedPostOne;
    private Post savedPostTwo;
    private Long loginedId;
    
    @BeforeEach
    void prepare(){
        //member
        final Member givenMemberOne =  Member.builder()
                                        .username("dlwlrma")
                                        .name("?????????")
                                        .password("1234")
                                        .email("123@gmail.com")
                                        .build();
        final Member givenMemberTwo =  Member.builder()
                                        .username("dlwlrma1")
                                        .name("?????????")
                                        .password("1234")
                                        .email("123@gmail.com")
                                        .build();
        savedMemberOne = memberRepository.save(givenMemberOne);
        savedMemberTwo = memberRepository.save(givenMemberTwo);
        loginedId = savedMemberOne.getId();

        //post
        Post postOne = Post.builder()
                .content("test content1")
                .member(givenMemberOne)
                .build();
        Post postTwo = Post.builder()
                .content("test content2")
                .member(givenMemberOne)
                .build();
        savedPostOne = postRepository.save(postOne);
        savedPostTwo = postRepository.save(postTwo);

        //post image
        List<Image> images = new ArrayList<>();
        images.add(ImageUtil.getBaseImage());
        images.add(ImageUtil.getBaseImage());
        List<String> strings = new ArrayList<>();
        strings.add("alt text1");
        strings.add("alt text2");
        postRepository.savePostImages(images, savedPostOne.getId(), strings);
        postRepository.savePostImages(images, savedPostTwo.getId(), strings);

        //post likes
        PostLike postLikeOne = PostLike.builder().member(savedMemberOne).post(savedPostOne).build();
        PostLike postLikeTwo = PostLike.builder().member(savedMemberTwo).post(savedPostOne).build();
        postLikeRepository.save(postLikeOne);
        postLikeRepository.save(postLikeTwo);

        //comment 
        Comment commentOne = Comment.builder().member(savedMemberOne).post(savedPostOne).parent(null)
                                                .content("test comment1").build();
        Comment commentTwo = Comment.builder().member(savedMemberTwo).post(savedPostOne).parent(null)
                                                .content("test comment2").build();
        commentRepository.save(commentOne);
        commentRepository.save(commentTwo);
    }
    @Nested
    @DisplayName("getRecent15PostDTOs ????????????")
    class Describe_getRecent15PostDTOs{
        @Nested
        @DisplayName("???????????? ????????? ????????????")
        class Context_logined{
            @Test
            @DisplayName("???????????? ????????? ????????????")
            void it_returns_well(){
                List<MemberPostDto> memberPostDtos = memberRepository.getRecent15PostDTOs(loginedId, savedMemberOne.getUsername());
                assertEquals(2, memberPostDtos.size());
                assertEquals(2, memberPostDtos.get(1).getPostLikesCount());
                assertEquals(2, memberPostDtos.get(1).getPostCommentsCount());
                assertTrue(memberPostDtos.get(1).isHasManyPosts());
            }   
        }
        @Nested
        @DisplayName("??????????????? ?????? ????????? ????????????")
        class Context_unlogined{
            @Test
            @DisplayName("???????????? ????????? ????????????")
            void it_returns_well(){
                List<MemberPostDto> memberPostDtos = memberRepository.getRecent15PostDTOs(-1L, savedMemberOne.getUsername());
                assertEquals(2, memberPostDtos.size());
                assertEquals(2, memberPostDtos.get(1).getPostLikesCount());
                assertEquals(2, memberPostDtos.get(1).getPostCommentsCount());
                assertTrue(memberPostDtos.get(1).isHasManyPosts());
            }   
        }
    }
    @Nested
    @DisplayName("getRecent15SavedPostDTOs ????????????")
    class Describe_getRecent15SavedPostDTOs{

        @BeforeEach
        void prepare_bookmark(){
            Bookmark bookmarkOne = Bookmark.builder().member(savedMemberOne).post(savedPostOne).build();
            Bookmark bookmarkTwo = Bookmark.builder().member(savedMemberOne).post(savedPostTwo).build();
            bookmarkRepository.save(bookmarkOne);
            bookmarkRepository.save(bookmarkTwo);
        }
        @Nested
        @DisplayName("???????????? ????????? ????????????")
        class Context_logined{
            @Test
            @DisplayName("???????????? ????????? ????????????")
            void it_returns_well(){
                List<MemberPostDto> memberPostDtos = memberRepository.getRecent15SavedPostDTOs(loginedId);
                assertEquals(2, memberPostDtos.size());
                assertEquals(2, memberPostDtos.get(1).getPostLikesCount());
                assertEquals(2, memberPostDtos.get(1).getPostCommentsCount());
                assertTrue(memberPostDtos.get(1).isHasManyPosts());

            }   
        }
    }

    @Nested
    @DisplayName("getRecent15TaggedPostDTOs ????????????")
    class Describe_getRecent15TaggedPostDTOs{
        @BeforeEach
        void prepare_tag() throws Exception{
            // Protected NoArgsConstructor??? ????????? ????????? ?????? Reflection??? ????????? ????????? ?????? ??????????????? ??????
            // ? ?????????/JUnit ?????? ???????????? ???????????? ??? ????????? ???????????? ????????? ?????????????????? ?????? ??????
            Constructor<PostImageTagRequest> postImageTagRequestConstructor = PostImageTagRequest.class.getDeclaredConstructor();
            postImageTagRequestConstructor.setAccessible(true);

            List<PostImage> postImagesOne = postImageRepository.findAllByPostId(savedPostOne.getId());
            List<PostImage> postImagesTwo = postImageRepository.findAllByPostId(savedPostTwo.getId());
            List<PostImageTagRequest> imageTagRequests = new ArrayList<>();
            
            PostImageTagRequest imageTagRequest = (PostImageTagRequest)postImageTagRequestConstructor.newInstance();
            imageTagRequest.setId(postImagesOne.get(0).getId());
            imageTagRequest.setTagX(50L);
            imageTagRequest.setTagY(50L);
            imageTagRequest.setUsername(savedMemberOne.getUsername());
            imageTagRequests.add(imageTagRequest);

            imageTagRequest = (PostImageTagRequest)postImageTagRequestConstructor.newInstance();
            imageTagRequest.setId(postImagesOne.get(1).getId());
            imageTagRequest.setTagX(50L);
            imageTagRequest.setTagY(50L);
            imageTagRequest.setUsername(savedMemberOne.getUsername());
            imageTagRequests.add(imageTagRequest);

            imageTagRequest = (PostImageTagRequest)postImageTagRequestConstructor.newInstance();
            imageTagRequest.setId(postImagesTwo.get(0).getId());
            imageTagRequest.setTagX(50L);
            imageTagRequest.setTagY(50L);
            imageTagRequest.setUsername(savedMemberOne.getUsername());
            imageTagRequests.add(imageTagRequest);
            postRepository.savePostTags(imageTagRequests);
        }
        @Nested
        @DisplayName("???????????? ????????? ????????????")
        class Context_logined{
            @Test
            @DisplayName("???????????? ????????? ????????????")
            void it_returns_well(){
                List<MemberPostDto> memberPostDtos = memberRepository.getRecent15TaggedPostDTOs(loginedId, savedMemberOne.getUsername());
                assertEquals(2, memberPostDtos.size());
                assertEquals(2, memberPostDtos.get(1).getPostLikesCount());
                assertEquals(2, memberPostDtos.get(1).getPostCommentsCount());
                assertTrue(memberPostDtos.get(1).isHasManyPosts());
            }   
        }
        @Nested
        @DisplayName("??????????????? ?????? ????????? ????????????")
        class Context_unlogined{
            @Test
            @DisplayName("???????????? ????????? ????????????")
            void it_returns_well(){
                List<MemberPostDto> memberPostDtos = memberRepository.getRecent15TaggedPostDTOs(-1L, savedMemberOne.getUsername());
                assertEquals(2, memberPostDtos.size());
                assertEquals(2, memberPostDtos.get(1).getPostLikesCount());
                assertEquals(2, memberPostDtos.get(1).getPostCommentsCount());
                assertTrue(memberPostDtos.get(1).isHasManyPosts());
            }   
        }
    }

}
