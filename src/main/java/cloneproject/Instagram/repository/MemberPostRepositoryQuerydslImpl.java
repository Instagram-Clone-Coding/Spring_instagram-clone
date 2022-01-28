package cloneproject.Instagram.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.dto.post.PostImageDTO;
import cloneproject.Instagram.dto.post.QMemberPostDTO;
import cloneproject.Instagram.dto.post.QPostImageDTO;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.member.QBlock.block;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;
import static cloneproject.Instagram.entity.post.QBookmark.bookmark;
import static cloneproject.Instagram.entity.post.QPostTag.postTag;;

@RequiredArgsConstructor
public class MemberPostRepositoryQuerydslImpl implements MemberPostRepositoryQuerydsl{
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<MemberPostDTO> getRecent15PostDTOs(Long loginedUserId, String username){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                post.id, 
                                                post.postImages.size().gt(1), 
                                                post.comments.size(), 
                                                post.postLikes.size()))
                                            .from(post, member)
                                            .where(post.member.username.eq(username))
                                            .limit(15)
                                            .orderBy(post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
                                        
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));
        return posts;
    }
    
    @Override
    public Page<MemberPostDTO> getMemberPostDto(Long loginedUserId, String username, Pageable pageable){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                post.id, 
                                                post.postImages.size().gt(1), 
                                                post.comments.size(), 
                                                post.postLikes.size()))
                                            .from(post)
                                            .where(post.member.username.eq(username))
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .orderBy(post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
    
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));    
                            
        
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public List<MemberPostDTO> getRecent15SavedPostDTOs(Long loginedUserId){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                bookmark.post.id, 
                                                bookmark.post.postImages.size().gt(1), 
                                                bookmark.post.comments.size(), 
                                                bookmark.post.postLikes.size()))
                                            .from(bookmark)
                                            .where(bookmark.member.id.eq(loginedUserId))
                                            .limit(15)
                                            .orderBy(bookmark.post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
                                        
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));
        return posts;
    }

    @Override
    public Page<MemberPostDTO> getMemberSavedPostDto(Long loginedUserId, Pageable pageable){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                bookmark.post.id, 
                                                bookmark.post.postImages.size().gt(1), 
                                                bookmark.post.comments.size(), 
                                                bookmark.post.postLikes.size()))
                                            .from(bookmark)
                                            .where(bookmark.member.id.eq(loginedUserId))
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .orderBy(bookmark.post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
    
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));    
                            
        
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public List<MemberPostDTO> getRecent15TaggedPostDTOs(Long loginedUserId, String username){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                postTag.postImage.post.id, 
                                                postTag.postImage.post.postImages.size().gt(1), 
                                                postTag.postImage.post.comments.size(), 
                                                postTag.postImage.post.postLikes.size()))
                                            .from(postTag)
                                            .where(postTag.tag.username.eq(username))
                                            .limit(15)
                                            .orderBy(postTag.postImage.post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
                                        
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));
        return posts;
    }

    @Override
    public Page<MemberPostDTO> getMemberTaggedPostDto(Long loginedUserId, String username, Pageable pageable){
        final List<MemberPostDTO> posts = queryFactory
                                            .select(new QMemberPostDTO(
                                                postTag.postImage.post.id, 
                                                postTag.postImage.post.postImages.size().gt(1), 
                                                postTag.postImage.post.comments.size(), 
                                                postTag.postImage.post.postLikes.size()))
                                            .from(postTag)
                                            .where(postTag.tag.username.eq(username))
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .orderBy(postTag.postImage.post.id.desc())
                                            .distinct()
                                            .fetch();
        
        final List<Long> postIds = posts.stream()
                                    .map(MemberPostDTO::getPostId)
                                    .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                                    .select(new QPostImageDTO(
                                            postImage.post.id,
                                            postImage.id,
                                            postImage.image.imageUrl
                                    ))
                                    .from(postImage)
                                    .where(postImage.post.id.in(postIds))
                                    .fetch();

        final Map<Long, List<PostImageDTO>> postImageDTOMap = postImageDTOs.stream()
                                                                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
    
        posts.forEach(p->p.setImageUrl(postImageDTOMap.get(p.getPostId()).get(0).getPostImageUrl()));    
                            
        
        return new PageImpl<>(posts, pageable, posts.size());
    }
    
}
